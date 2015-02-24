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
package org.krysalis.barcode4j.impl;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMailBean;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProviderTest;

/**
 *
 * @author mk
 */
public class AbstractVariableHeightLogicHandlerTest {

    HeightVariableBarcodeBean bean;
    Canvas canvas;
    SVGCanvasProvider svg;
    AbstractVariableHeightLogicHandler instance;

    @Before
    public void setUp() {
        svg = new SVGCanvasProvider(Orientation.ZERO);
        canvas = new Canvas(svg);
        bean = new USPSIntelligentMailBean();
        bean.setModuleWidth(1);
        instance = new TestImpl(bean, canvas);
    }

    private static class TestImpl extends AbstractVariableHeightLogicHandler {

        public TestImpl(HeightVariableBarcodeBean bcBean, Canvas canvas) {
            super(bcBean, canvas);
        }

        @Override
        public void addBar(boolean black, int weight) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    /**
     * Test of setTextAlignment method, of class
     * AbstractVariableHeightLogicHandler.
     */
    @Test
    public void testSetTextAlignment() {
        System.out.println("setTextAlignment");
        TextAlignment align = TextAlignment.TA_CENTER;
        instance.setTextAlignment(align);
        assertTrue("void method should not throw an exception", true);
    }

    @Test(expected = NullPointerException.class)
    public void testSetTextAlignmentFail() {
        System.out.println("setTextAlignment");
        TextAlignment align = null;
        instance.setTextAlignment(align);
    }

    /**
     * Test of startBarcode method, of class AbstractVariableHeightLogicHandler.
     */
    @Test
    public void testStartBarcode() {
        System.out.println("startBarcode");
        String expected = "<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"5.2324mm\" viewBox=\"0 0 111.99 5.2324\" width=\"111.99mm\"><g fill=\"black\" stroke=\"none\"/></svg>";
        instance.startBarcode("Barcode4J", null);
        assertEquals(expected, SVGCanvasProviderTest.getStringFromDoc(svg.getDOM()));
    }

    /**
     * Test of getTextY method, of class AbstractVariableHeightLogicHandler.
     */
    @Test
    public void testGetTextY() {
        System.out.println("getTextY");
        bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        assertEquals(0.0, instance.getTextY(), 0.0);
        bean.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        assertEquals(7.34, instance.getTextY(), 0.01);
        bean.setMsgPosition(HumanReadablePlacement.HRP_TOP);
        assertEquals(2.82, instance.getTextY(), 0.01);
    }

    /**
     * Test of endBarcode method, of class AbstractVariableHeightLogicHandler.
     */
    @Test
    public void testEndBarcodeHrpNone() {
        System.out.println("endBarcode");
        String expected = "<svg xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"black\" stroke=\"none\"/></svg>";
        bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        instance.endBarcode();
        assertEquals(expected, SVGCanvasProviderTest.getStringFromDoc(svg.getDOM()));
    }

    @Test
    public void testEndBarcode() {
        System.out.println("endBarcode");
        String expected = "<svg xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"black\" stroke=\"none\"><text font-family=\"Helvetica\" font-size=\"2.8219\" text-anchor=\"middle\" x=\"1.5875\" y=\"7.144\"/></g></svg>";
        bean.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        instance.endBarcode();
        assertEquals(expected, SVGCanvasProviderTest.getStringFromDoc(svg.getDOM()));
    }

    /**
     * Test of startBarGroup method, of class
     * AbstractVariableHeightLogicHandler.
     */
    @Test
    public void testStartBarGroup() {
        System.out.println("startBarGroup");
        String expected = "<svg xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"black\" stroke=\"none\"/></svg>";
        instance.startBarGroup(BarGroup.START_CHARACTER, "startBarGroup");
        assertEquals(expected, SVGCanvasProviderTest.getStringFromDoc(svg.getDOM()));
    }

    /**
     * Test of endBarGroup method, of class AbstractVariableHeightLogicHandler.
     */
    @Test
    public void testEndBarGroup() {
        System.out.println("endBarGroup");
        String expected = "<svg xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"black\" stroke=\"none\"/></svg>";
        instance.endBarGroup();
        assertEquals(expected, SVGCanvasProviderTest.getStringFromDoc(svg.getDOM()));
    }
}
