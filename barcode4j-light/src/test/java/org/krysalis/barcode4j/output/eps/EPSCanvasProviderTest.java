/*
 * Copyright 2015 mk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.krysalis.barcode4j.output.eps;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.Orientation;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author mk
 */
public class EPSCanvasProviderTest {

    OutputStream out = null;
    Orientation orientation = Orientation.ZERO;

    @Before
    public void setUp() {
        out = new ByteArrayOutputStream(1024);
    }

    /**
     * Test of getDecimalFormat method, of class EPSCanvasProvider.
     */
    @Test
    public void testGetDecimalFormat() throws Exception {
        System.out.println("getDecimalFormat");
        EPSCanvasProvider instance = new EPSCanvasProvider(out, orientation);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        DecimalFormat expResult = new DecimalFormat("0.####", dfs);
        DecimalFormat result = instance.getDecimalFormat();
        assertEquals(expResult, result);
    }

    /**
     * Test of finish method, of class EPSCanvasProvider.
     */
    @Test
    public void testFinish() throws Exception {
        System.out.println("finish");
        EPSCanvasProvider instance = new EPSCanvasProvider(out, orientation);
        instance.finish();
        assertEquals("%%EOF\n", ((ByteArrayOutputStream) out).toString("US-ASCII"));
    }

    /**
     * Test of establishDimensions method, of class EPSCanvasProvider.
     */
    @Test
    public void testEstablishDimensions() throws Exception {
        System.out.println("establishDimensions");
        BarcodeDimension dim = new BarcodeDimension(100, 100);
        EPSCanvasProvider instance = new EPSCanvasProvider(out, orientation);
        instance.establishDimensions(dim);
        instance.finish();
        String result = ((ByteArrayOutputStream) out).toString("US-ASCII");
        assertThat(result, startsWith("%!PS-Adobe-3.0 EPSF-3.0\n%%BoundingBox: 0 0 284 284\n%%HiResBoundingBox: 0 0 283.5 283.5\n"));
        assertThat(result, endsWith("%%EndProlog\n%%EOF\n"));
    }

    @Test
    public void testEstablishDimensionsRotate() throws Exception {
        System.out.println("establishDimensions");
        BarcodeDimension dim = new BarcodeDimension(100, 200);
        EPSCanvasProvider instance = new EPSCanvasProvider(out, Orientation.NINETY);
        instance.establishDimensions(dim);
        instance.finish();
        String result = ((ByteArrayOutputStream) out).toString("US-ASCII");

        assertThat(result, startsWith("%!PS-Adobe-3.0 EPSF-3.0\n%%BoundingBox: 0 0 567 284\n%%HiResBoundingBox: 0 0 567 283.5\n"));
        assertThat(result, endsWith("%%EndProlog\n90 rotate 0 -567 translate\n%%EOF\n"));
    }

    /**
     * Test of deviceFillRect method, of class EPSCanvasProvider.
     */
    @Test
    public void testDeviceFillRect() throws Exception {
        System.out.println("deviceFillRect");
        double x = 0.0;
        double y = 0.0;
        double w = 10.0;
        double h = 10.0;
        EPSCanvasProvider instance = new EPSCanvasProvider(out, orientation);
        instance.deviceFillRect(x, y, w, h);
        instance.finish();
        String result = ((ByteArrayOutputStream) out).toString("US-ASCII");
        assertThat(result, startsWith("0 0 28.35 28.35 rf"));
    }

    /**
     * Test of deviceText method, of class EPSCanvasProvider.
     */
    @Test
    public void testDeviceText() throws Exception {
        System.out.println("deviceText");
        String text = "Barcode4J";
        double x1 = 0.0;
        double x2 = 100.0;
        double y1 = 0.0;
        String fontName = "Tahoma";
        double fontSize = 10.0;
        EPSCanvasProvider instance = new EPSCanvasProvider(out, orientation);
        instance.deviceText(text, x1, x2, y1, fontName, fontSize, TextAlignment.TA_CENTER);
        instance.deviceText(text, x1, x2, y1, fontName, fontSize, TextAlignment.TA_JUSTIFY);
        instance.deviceText(text, x1, x2, y1, fontName, fontSize, TextAlignment.TA_LEFT);
        instance.deviceText(text, x1, x2, y1, fontName, fontSize, TextAlignment.TA_RIGHT);
        instance.finish();
        String result = ((ByteArrayOutputStream) out).toString("US-ASCII");

        assertThat(result, startsWith("/Tahoma findfont 28.35 scalefont setfont"));
        assertThat(result, containsString("(Barcode4J) 141.75 0 ct"));
        assertThat(result, containsString("(Barcode4J) 0 283.5 0 jt"));
        assertThat(result, containsString("0 0 moveto (Barcode4J) show"));
        assertThat(result, containsString("(Barcode4J) 0 283.5 0 rt"));
    }
}
