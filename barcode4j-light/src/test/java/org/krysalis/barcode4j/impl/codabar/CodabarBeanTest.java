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
package org.krysalis.barcode4j.impl.codabar;

import org.junit.Test;
import static org.junit.Assert.*;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;

/**
 *
 * @author mk
 */
public class CodabarBeanTest {
    
    public CodabarBeanTest() {
    }

    /**
     * Test of getChecksumMode method, of class CodabarBean.
     */
    @Test
    public void testGetChecksumMode() {
        System.out.println("getChecksumMode");
        CodabarBean instance = new CodabarBean();
        ChecksumMode expResult = ChecksumMode.CP_AUTO;
        ChecksumMode result = instance.getChecksumMode();
        assertEquals(expResult, result);
    }

    /**
     * Test of setChecksumMode method, of class CodabarBean.
     */
    @Test
    public void testSetChecksumMode() {
        System.out.println("setChecksumMode");
        ChecksumMode mode = null;
        CodabarBean instance = new CodabarBean();
        instance.setChecksumMode(mode);
    }

    /**
     * Test of getWideFactor method, of class CodabarBean.
     */
    @Test
    public void testGetWideFactor() {
        System.out.println("getWideFactor");
        CodabarBean instance = new CodabarBean();
        double expResult = 3.0;
        double result = instance.getWideFactor();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setWideFactor method, of class CodabarBean.
     */
    @Test
    public void testSetWideFactor() {
        System.out.println("setWideFactor");
        double value = 1.01;
        CodabarBean instance = new CodabarBean();
        instance.setWideFactor(value);
    }

    /**
     * Test of getBarWidth method, of class CodabarBean.
     */
    @Test
    public void testGetBarWidth() {
        System.out.println("getBarWidth");
        int width = 1;
        CodabarBean instance = new CodabarBean();
        double expResult = 0.20;
        double result = instance.getBarWidth(width);
        assertEquals(expResult, result, 0.01);
    }

    /**
     * Test of isDisplayStartStop method, of class CodabarBean.
     */
    @Test
    public void testIsDisplayStartStop() {
        System.out.println("isDisplayStartStop");
        CodabarBean instance = new CodabarBean();
        boolean expResult = false;
        boolean result = instance.isDisplayStartStop();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDisplayStartStop method, of class CodabarBean.
     */
    @Test
    public void testSetDisplayStartStop() {
        System.out.println("setDisplayStartStop");
        boolean value = false;
        CodabarBean instance = new CodabarBean();
        instance.setDisplayStartStop(value);
    }

    /**
     * Test of generateBarcode method, of class CodabarBean.
     */
    @Test
    public void testGenerateBarcode() {
        System.out.println("generateBarcode");
        CanvasProvider canvas = new SVGCanvasProvider(Orientation.ZERO);
        String msg = "12345";
        CodabarBean instance = new CodabarBean();
        instance.generateBarcode(canvas, msg);
    }

    /**
     * Test of calcDimensions method, of class CodabarBean.
     */
    @Test
    public void testCalcDimensions() {
        System.out.println("calcDimensions");
        String msg = "12345";
        CodabarBean instance = new CodabarBean();
        instance.setModuleWidth(1);
        BarcodeDimension expResult = new BarcodeDimension(59.0, 17.822);
        BarcodeDimension result = instance.calcDimensions(msg);
        assertEquals(expResult.getWidth(), result.getWidth(), 0.01);
        assertEquals(expResult.getHeight(), result.getHeight(), 0.01);
    }
}
