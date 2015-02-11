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
package org.krysalis.barcode4j.output;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mk
 */
public class OrientationTest {

    /**
     * Test of getDegrees method, of class Orientation.
     */
    @Test
    public void getDegrees() {
        System.out.println("getDegrees");
        assertEquals(0, Orientation.ZERO.getDegrees());
        assertEquals(90, Orientation.NINETY.getDegrees());
        assertEquals(180, Orientation.ONEHUNDRED_EIGHTY.getDegrees());
        assertEquals(270, Orientation.TWOHUNDRED_SEVENTY.getDegrees());
    }

    /**
     * Test of isSwitched method, of class Orientation.
     */
    @Test
    public void isSwitched() {
        System.out.println("isSwitched");
        assertEquals(false, Orientation.ZERO.isSwitched());
        assertEquals(true, Orientation.NINETY.isSwitched());
        assertEquals(false, Orientation.ONEHUNDRED_EIGHTY.isSwitched());
        assertEquals(true, Orientation.TWOHUNDRED_SEVENTY.isSwitched());
    }

    /**
     * Test of fromInt method, of class Orientation.
     */
    @Test
    public void fromInt() {
        assertEquals(Orientation.ZERO, Orientation.fromInt(0));
        assertEquals(Orientation.TWOHUNDRED_SEVENTY, Orientation.fromInt(270));
        assertEquals(Orientation.TWOHUNDRED_SEVENTY, Orientation.fromInt(270 + 360));
        assertEquals(Orientation.TWOHUNDRED_SEVENTY, Orientation.fromInt(270 - 360));
        
        try {
            Orientation.fromInt(45);
            fail("Orientation 45 not allowed.");
        } catch (IllegalArgumentException e) {}
    }

}
