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
package org.krysalis.barcode4j.impl.datamatrix;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mk
 */
public class SymbolShapeHintTest {

    /**
     * Test of getName method, of class SymbolShapeHint.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        assertEquals("force-none", SymbolShapeHint.FORCE_NONE.getName());
    }

    /**
     * Test of byName method, of class SymbolShapeHint.
     */
    @Test
    public void testByName() {
        System.out.println("byName");
        assertEquals(SymbolShapeHint.FORCE_RECTANGLE, SymbolShapeHint.byName("force-rectangle"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void testByNameFail() {
        System.out.println("byNameFail");
        SymbolShapeHint.byName("foo");
    }

    /**
     * Test of toString method, of class SymbolShapeHint.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        assertEquals("force-square", SymbolShapeHint.FORCE_SQUARE.toString());
    }
    
}
