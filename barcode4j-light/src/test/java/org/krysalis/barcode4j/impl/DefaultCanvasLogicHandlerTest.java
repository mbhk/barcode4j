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

import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProviderTest;

/**
 *
 * @author mk
 */
public class DefaultCanvasLogicHandlerTest {

    private static class BarcodeBeanTest extends AbstractBarcodeBean {

        @Override
        public BarcodeDimension calcDimensions(String msg) {
            return new BarcodeDimension(100, 100);
        }

        @Override
        public void generateBarcode(CanvasProvider canvas, String msg) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Collection<String> getAdditionalNames() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double getBarWidth(int width) {
            return width;
        }

        @Override
        public String getId() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private Canvas canvas;
    private SVGCanvasProvider svg;
    private AbstractBarcodeBean bean;
    DefaultCanvasLogicHandler instance;

    @Before
    public void setUp() {
        svg = new SVGCanvasProvider(Orientation.ZERO);
        canvas = new Canvas(svg);
        bean = new BarcodeBeanTest();
        instance = new DefaultCanvasLogicHandler(bean, canvas);
    }

    private String getDomString() {
        return SVGCanvasProviderTest.getStringFromDoc(svg.getDOM());
    }

    /**
     * Test of getStartX method, of class DefaultCanvasLogicHandler.
     */
    @Test
    public void testGetStartX() {
        System.out.println("getStartX");
        assertEquals(0.0, instance.getStartX(), 0.0);
        bean.setQuietZone(20);
        assertEquals(20.0, instance.getStartX(), 0.0);
    }

    /**
     * Test of getStartY method, of class DefaultCanvasLogicHandler.
     */
    @Test
    public void testGetStartY() {
        System.out.println("getStartY");
        assertEquals(0.0, instance.getStartY(), 0.0);
        bean.setVerticalQuietZone(20);
        assertEquals(20.0, instance.getStartY(), 0.0);
        bean.setMsgPosition(HumanReadablePlacement.HRP_TOP);
        assertEquals(22.82, instance.getStartY(), 0.01);
    }

    /**
     * Test of startBarcode method, of class DefaultCanvasLogicHandler.
     */
    @Test
    public void testStartBarcode() {
        System.out.println("startBarcode");
        instance.startBarcode("foo", null);
        assertEquals("<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"100mm\" viewBox=\"0 0 100 100\" width=\"100mm\"><g fill=\"black\" stroke=\"none\"/></svg>", getDomString());
    }

    /**
     * Test of startBarGroup method, of class DefaultCanvasLogicHandler.
     */
    @Test
    public void testStartBarGroup() {
        System.out.println("startBarGroup");
        String submsg = "";
        instance.startBarGroup(BarGroup.STOP_CHARACTER, submsg);
        assertEquals("<svg xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"black\" stroke=\"none\"/></svg>", getDomString());
    }

    /**
     * Test of addBar method, of class DefaultCanvasLogicHandler.
     */
    @Test
    public void testAddBar() {
        System.out.println("addBar");
        boolean black = false;
        int width = 10;
        instance.addBar(black, width);
        instance.addBar(!black, width);
        assertEquals("<svg xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"black\" stroke=\"none\"><rect height=\"15\" width=\"10\" x=\"10\" y=\"0\"/></g></svg>", getDomString());
    }

    /**
     * Test of endBarGroup method, of class DefaultCanvasLogicHandler.
     */
    @Test
    public void testEndBarGroup() {
        System.out.println("endBarGroup");
        instance.endBarGroup();
        assertEquals("<svg xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"black\" stroke=\"none\"/></svg>", getDomString());
    }

    /**
     * Test of endBarcode method, of class DefaultCanvasLogicHandler.
     */
    @Test
    public void testEndBarcode() {
        System.out.println("endBarcode");
        instance.endBarcode();
    }

    /**
     * Test of getTextBaselinePosition method, of class
     * DefaultCanvasLogicHandler.
     */
    @Test
    public void testGetTextBaselinePosition() {
        System.out.println("getTextBaselinePosition");
        double expResult = 0.0;
        double result = instance.getTextBaselinePosition();
        // TODO
        //assertEquals(expResult, result, 0.0);
    }
}
