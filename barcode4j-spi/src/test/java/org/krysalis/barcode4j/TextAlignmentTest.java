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

import junit.framework.TestCase;

/**
 *
 * @author mk
 */
public class TextAlignmentTest extends TestCase {

    /**
     * Test of getName method, of class TextAlignment.
     */
    public void testGetName() {
        System.out.println("getName");
        assertEquals("center", TextAlignment.TA_CENTER.getName());
    }

    /**
     * Test of byName method, of class TextAlignment.
     */
    public void testByName() {
        assertEquals(TextAlignment.TA_JUSTIFY, TextAlignment.byName("justify"));

        try {
            TextAlignment.byName("foo");
            fail("foo not convertable to TextAlignment");
        } catch (IllegalArgumentException e) {
        }
    }
}
