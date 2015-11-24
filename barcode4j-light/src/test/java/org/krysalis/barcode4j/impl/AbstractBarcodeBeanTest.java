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

import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.CanvasProvider;

/**
 *
 * @author mk
 */
public class AbstractBarcodeBeanTest {

    /**
     * Test of setPattern method, of class AbstractBarcodeBean.
     */
    @Test
    public void testSetPattern() {
        System.out.println("setPattern");
        String v = "___";
        AbstractBarcodeBean instance = new AbstractBarcodeBeanImpl();
        instance.setPattern(v);
        assertEquals(v, instance.getPattern());
    }

    /**
     * Test of getHumanReadableHeight method, of class AbstractBarcodeBean.
     */
    @Test
    public void testGetHumanReadableHeight() {
        System.out.println("getHumanReadableHeight");
        AbstractBarcodeBean instance = new AbstractBarcodeBeanImpl();
        double expResult = 0.0;
        instance.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        double result = instance.getHumanReadableHeight();
        assertEquals(expResult, result, 0.0);
        instance.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
        result = instance.getHumanReadableHeight();
        assertEquals(3.67, result, 0.01);
    }

    /**
     * Test of getVerticalQuietZone method, of class AbstractBarcodeBean.
     */
    @Test
    public void testGetVerticalQuietZone() {
        System.out.println("getVerticalQuietZone");
        AbstractBarcodeBean instance = new AbstractBarcodeBeanImpl();
        double expResult = 10.0;
        instance.setQuietZone(expResult);
        double result = instance.getVerticalQuietZone();
        assertEquals(expResult, result, 0.0);
        instance.setVerticalQuietZone(expResult * 2);
        result = instance.getVerticalQuietZone();
        assertEquals(expResult * 2, result, 0.0);
    }

    /**
     * Test of setFontName method, of class AbstractBarcodeBean.
     */
    @Test
    public void testSetFontName() {
        System.out.println("setFontName");
        String name = "Tahoma";
        AbstractBarcodeBean instance = new AbstractBarcodeBeanImpl();
        instance.setFontName(name);
        assertEquals(name, instance.getFontName());
    }

    /**
     * Test of calcDimensions method, of class AbstractBarcodeBean.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testCalcDimensions() {
        System.out.println("calcDimensions");
        String msg = "";
        AbstractBarcodeBean instance = new AbstractBarcodeBeanImpl();
        instance.calcDimensions(msg);
    }

    public class AbstractBarcodeBeanImpl extends AbstractBarcodeBean {

        @Override
        protected boolean hasFontDescender() {
            return true;
        }
        
        @Override
        public Collection<String> getAdditionalNames() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public double getBarWidth(int width) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void generateBarcode(CanvasProvider canvas, String msg) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getId() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
