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
public class BaselineAlignmentTest extends TestCase {

    public BaselineAlignmentTest(String testName) {
        super(testName);
    }

    /**
     * Test of getName method, of class BaselineAlignment.
     */
    public void testGetName() {
        System.out.println("getName");
        assertEquals("bottom", BaselineAlignment.ALIGN_BOTTOM.getName());
        assertEquals("top", BaselineAlignment.ALIGN_TOP.getName());
    }

    /**
     * Test of byName method, of class BaselineAlignment.
     */
    public void testByName() {
        System.out.println("byName");
        assertEquals(BaselineAlignment.ALIGN_TOP, BaselineAlignment.byName("TOP"));
        assertEquals(BaselineAlignment.ALIGN_BOTTOM, BaselineAlignment.byName("bottoM"));

        try {
            BaselineAlignment.byName("foo");
            fail("foo is no BaselineAlignment");
        } catch (IllegalArgumentException e) {
        }
    }
}
