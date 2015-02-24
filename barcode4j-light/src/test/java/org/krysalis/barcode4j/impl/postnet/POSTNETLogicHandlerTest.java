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
package org.krysalis.barcode4j.impl.postnet;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.krysalis.barcode4j.BaselineAlignment;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProviderTest;

/**
 *
 * @author mk
 */
public class POSTNETLogicHandlerTest {

    POSTNETBean bean;
    Canvas canvas;
    SVGCanvasProvider svg;
    POSTNETLogicHandler instance;

    @Before
    public void setUp() {
        svg = new SVGCanvasProvider(Orientation.ZERO);
        canvas = new Canvas(svg);
        bean = new POSTNETBean();
        instance = new POSTNETLogicHandler(bean, canvas);
    }

    /**
     * Test of startBarcode method, of class POSTNETLogicHandler.
     */
    @Test
    public void testStartBarcode() {
        System.out.println("startBarcode");
        String expected = "<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"5.207mm\" viewBox=\"0 0 53.594 5.207\" width=\"53.594mm\"><g fill=\"black\" stroke=\"none\"/></svg>";
        instance.startBarcode("1234-5678-0", null);

        System.out.println(SVGCanvasProviderTest.getStringFromDoc(svg.getDOM()));
        assertEquals(expected, SVGCanvasProviderTest.getStringFromDoc(svg.getDOM()));
    }

    /**
     * Test of addBar method, of class POSTNETLogicHandler.
     */
    @Test
    public void testAddBarDefault() {
        System.out.println("addBar");
        String expected = "<svg xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"black\" stroke=\"none\"><rect height=\"3.175\" width=\"0.508\" x=\"0.508\" y=\"0\"/><rect height=\"1.27\" width=\"0.508\" x=\"1.524\" y=\"1.905\"/><rect height=\"3.175\" width=\"0.508\" x=\"2.54\" y=\"0\"/></g></svg>";
        boolean black = false;
        instance.addBar(black, -1);
        instance.addBar(!black, -1);
        instance.addBar(black, 1);
        instance.addBar(!black, 1);
        instance.addBar(black, 2);
        instance.addBar(!black, 2);
        assertEquals(expected, SVGCanvasProviderTest.getStringFromDoc(svg.getDOM()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddBarFail() {
        System.out.println("addBar");
        boolean black = false;
        instance.addBar(black, 0);
    }

    @Test
    public void testAddBarPlacement() {
        System.out.println("addBar");
        String expected = "<svg xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"black\" stroke=\"none\"><rect height=\"3.175\" width=\"0.508\" x=\"0.508\" y=\"2.8219\"/><rect height=\"1.27\" width=\"0.508\" x=\"1.524\" y=\"4.7269\"/><rect height=\"3.175\" width=\"0.508\" x=\"2.54\" y=\"0\"/></g></svg>";
        boolean black = false;
        bean.setMsgPosition(HumanReadablePlacement.HRP_TOP);
        bean.setBaselinePosition(BaselineAlignment.ALIGN_TOP);
        instance.addBar(black, -1);
        instance.addBar(!black, -1);
        bean.setBaselinePosition(BaselineAlignment.ALIGN_BOTTOM);
        instance.addBar(black, 1);
        instance.addBar(!black, 1);
        bean.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        bean.setBaselinePosition(BaselineAlignment.ALIGN_TOP);
        instance.addBar(black, -1);
        instance.addBar(!black, -1);
        assertEquals(expected, SVGCanvasProviderTest.getStringFromDoc(svg.getDOM()));
    }
}
