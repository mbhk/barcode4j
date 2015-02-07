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
package org.krysalis.barcode4j.impl.code128;

import junit.framework.TestCase;

/**
 *
 * @author mk
 */
public class Code128ConstantsTest extends TestCase {

    /**
     * Test of fromInt method, of class Code128Constants.
     */
    public void testFromInt() {
        System.out.println("fromInt");
        int i = 7;
        Code128Constants result = Code128Constants.fromInt(i);
        assertEquals(Code128Constants.CODESET_ALL, result);
        
        try {
            Code128Constants.fromInt(0);
            fail("Must throw an exception for 0");
        } catch (IllegalArgumentException e) {}
    }

    /**
     * Test of and method, of class Code128Constants.
     */
    public void testAnd() {
        System.out.println("and");
        Code128Constants a = Code128Constants.CODESET_A;
        Code128Constants b = Code128Constants.CODESET_B;
        Code128Constants expResult = Code128Constants.CODESET_AB;
        Code128Constants result = Code128Constants.and(a, b);
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class Code128Constants.
     */
    public void testGetValue() {
        System.out.println("getValue");
        Code128Constants instance = Code128Constants.CODESET_BC;
        int expResult = 6;
        int result = instance.getValue();
        assertEquals(expResult, result);
    }
}
