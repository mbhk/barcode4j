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
package org.krysalis.barcode4j;

import java.awt.geom.Rectangle2D;
import junit.framework.TestCase;
import org.krysalis.barcode4j.output.Orientation;

/**
 *
 * @author mk
 */
public class BarcodeDimensionTest extends TestCase {
    
    public BarcodeDimensionTest(String testName) {
        super(testName);
    }

    /**
     * Test of getHeight method, of class BarcodeDimension.
     */
    public void testGetHeight_0args() {
        System.out.println("getHeight");
        assertEquals(200.0, new BarcodeDimension(100, 200).getHeight(), 0.0);
        
    }

    /**
     * Test of getHeight method, of class BarcodeDimension.
     */
    public void testGetHeight_Orientation() {
        System.out.println("getHeight");
        assertEquals(100.0, new BarcodeDimension(100, 200).getHeight(Orientation.NINETY), 0.0);
        assertEquals(200.0, new BarcodeDimension(100, 200).getHeight(Orientation.ONEHUNDRED_EIGHTY), 0.0);
    }

    /**
     * Test of getHeightPlusQuiet method, of class BarcodeDimension.
     */
    public void testGetHeightPlusQuiet_0args() {
        System.out.println("getHeightPlusQuiet");
        assertEquals(210.0, new BarcodeDimension(100, 200, 110, 210, 5, 5).getHeightPlusQuiet(), 0.0);
    }

    /**
     * Test of getHeightPlusQuiet method, of class BarcodeDimension.
     */
    public void testGetHeightPlusQuiet_Orientation() {
        System.out.println("getHeightPlusQuiet");
        assertEquals(210.0, new BarcodeDimension(100, 200, 110, 210, 5, 5).getHeightPlusQuiet(Orientation.ZERO), 0.0);
        assertEquals(110.0, new BarcodeDimension(100, 200, 110, 210, 5, 5).getHeightPlusQuiet(Orientation.TWOHUNDRED_SEVENTY), 0.0);
    }

    /**
     * Test of getWidth method, of class BarcodeDimension.
     */
    public void testGetWidth_0args() {
        System.out.println("getWidth");
        assertEquals(100.0, new BarcodeDimension(100, 200).getWidth(), 0.0);
    }

    /**
     * Test of getWidth method, of class BarcodeDimension.
     */
    public void testGetWidth_Orientation() {
        System.out.println("getWidth");
        assertEquals(100.0, new BarcodeDimension(100, 200).getWidth(Orientation.ZERO), 0.0);
        assertEquals(200.0, new BarcodeDimension(100, 200).getWidth(Orientation.NINETY), 0.0);
    }

    /**
     * Test of getWidthPlusQuiet method, of class BarcodeDimension.
     */
    public void testGetWidthPlusQuiet_0args() {
        System.out.println("getWidthPlusQuiet");
        assertEquals(110.0, new BarcodeDimension(100, 200, 110, 210, 5, 5).getWidthPlusQuiet(), 0.0);
    }

    /**
     * Test of getWidthPlusQuiet method, of class BarcodeDimension.
     */
    public void testGetWidthPlusQuiet_Orientation() {
        System.out.println("getWidthPlusQuiet");
        assertEquals(110.0, new BarcodeDimension(100, 200, 110, 210, 5, 5).getWidthPlusQuiet(Orientation.ONEHUNDRED_EIGHTY), 0.0);
        assertEquals(210.0, new BarcodeDimension(100, 200, 110, 210, 5, 5).getWidthPlusQuiet(Orientation.TWOHUNDRED_SEVENTY), 0.0);
    }

    /**
     * Test of getXOffset method, of class BarcodeDimension.
     */
    public void testGetXOffset() {
        System.out.println("getXOffset");
        assertEquals(5, new BarcodeDimension(100, 200, 110, 204, 5, 2).getXOffset(), 0.0);
        assertEquals(0, new BarcodeDimension(100, 200).getXOffset(), 0.0);
    }

    /**
     * Test of getYOffset method, of class BarcodeDimension.
     */
    public void testGetYOffset() {
        System.out.println("getYOffset");
        assertEquals(2, new BarcodeDimension(100, 200, 110, 204, 5, 2).getYOffset(), 0.0);
        assertEquals(0, new BarcodeDimension(100, 200).getYOffset(), 0.0);
    }

    /**
     * Test of getBoundingRect method, of class BarcodeDimension.
     */
    public void testGetBoundingRect() {
        System.out.println("getBoundingRect");
        BarcodeDimension instance = new BarcodeDimension(100, 200, 110, 204, 5, 2);
        Rectangle2D expResult = new Rectangle2D.Double(0, 0, 110, 204);
        Rectangle2D result = instance.getBoundingRect();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContentRect method, of class BarcodeDimension.
     */
    public void testGetContentRect() {
        System.out.println("getContentRect");
        BarcodeDimension instance = new BarcodeDimension(100, 200, 110, 204, 5, 2);
        Rectangle2D expResult = new Rectangle2D.Double(5, 2, 100, 200);
        Rectangle2D result = instance.getContentRect();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class BarcodeDimension.
     */
    public void testToString() {
        System.out.println("toString");
        BarcodeDimension instance = new BarcodeDimension(100, 200, 110, 204, 5, 2);
        String expResult = "[width=100.0(110.0),height=200.0(204.0)]";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
    
}
