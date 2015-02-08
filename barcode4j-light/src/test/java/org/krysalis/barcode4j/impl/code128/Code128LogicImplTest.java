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
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 *
 * @author mk
 */
public class Code128LogicImplTest extends TestCase {
    
    public Code128LogicImplTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of isValidChar method, of class Code128LogicImpl.
     */
    public void testIsValidChar() {
        System.out.println("isValidChar");
        char ch = 010;
        boolean expResult = true;
        boolean result = Code128LogicImpl.isValidChar(ch);
        assertEquals(expResult, result);
    }

    /**
     * Test of isInCodeSetA method, of class Code128LogicImpl.
     */
    public void testIsInCodeSetA() {
        System.out.println("isInCodeSetA");
        char ch = 'A';
        boolean expResult = true;
        boolean result = Code128LogicImpl.isInCodeSetA(ch);
        assertEquals(expResult, result);
    }

    /**
     * Test of isInCodeSetB method, of class Code128LogicImpl.
     */
    public void testIsInCodeSetB() {
        System.out.println("isInCodeSetB");
        char ch = ' ';
        boolean expResult = true;
        boolean result = Code128LogicImpl.isInCodeSetB(ch);
        assertEquals(expResult, result);
    }

    /**
     * Test of canBeInCodeSetC method, of class Code128LogicImpl.
     */
    public void testCanBeInCodeSetC() {
        System.out.println("canBeInCodeSetC");
        char ch = '1';
        boolean second = false;
        boolean expResult = true;
        boolean result = Code128LogicImpl.canBeInCodeSetC(ch, second);
        assertEquals(expResult, result);
    }

    /**
     * Test of symbolCharToString method, of class Code128LogicImpl.
     */
    public void testSymbolCharToString() {
        System.out.println("symbolCharToString");
        int index = 0;
        String expResult = "idx0";
        String result = Code128LogicImpl.symbolCharToString(index);
        assertEquals(expResult, result);
        assertEquals("FNC3/96", Code128LogicImpl.symbolCharToString(96));
    }

    /**
     * Test of toString method, of class Code128LogicImpl.
     */
    public void testToString() {
        System.out.println("toString");
        assertEquals("", Code128LogicImpl.toString(null));
        assertEquals("", Code128LogicImpl.toString(new int[] {}));
        assertEquals("StartC|idx13|idx37|CodeB/FNC4|idx45|idx65|idx84|idx69", Code128LogicImpl.toString(new int[] {105, 13, 37, 100, 45, 65, 84, 69}));
    }

    /**
     * Test of encodeChar method, of class Code128LogicImpl.
     */
    /*public void testEncodeChar() {
        System.out.println("encodeChar");
        ClassicBarcodeLogicHandler logic = null;
        int index = 0;
        Code128LogicImpl instance = new Code128LogicImpl();
        instance.encodeChar(logic, index);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of encodeStop method, of class Code128LogicImpl.
     */
    /*public void testEncodeStop() {
        System.out.println("encodeStop");
        ClassicBarcodeLogicHandler logic = null;
        Code128LogicImpl instance = new Code128LogicImpl();
        instance.encodeStop(logic);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getEncoder method, of class Code128LogicImpl.
     */
    public void testGetEncoder() {
        System.out.println("getEncoder");
        Code128LogicImpl instance = new Code128LogicImpl();
        Code128Encoder result = instance.getEncoder();
        assertNotNull(result);
    }

    /**
     * Test of createEncodedMessage method, of class Code128LogicImpl.
     */
    public void testCreateEncodedMessage() {
        System.out.println("createEncodedMessage");
        String msg = "1337Mate";
        Code128LogicImpl instance = new Code128LogicImpl();
        int[] expResult = new int[] {105, 13, 37, 100, 45, 65, 84, 69};
        int[] result = instance.createEncodedMessage(msg);
        assertEquals(expResult.length, result.length);
        for (int i = 0; i < result.length; i++) {
            assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of generateBarcodeLogic method, of class Code128LogicImpl.
     */
    /*public void testGenerateBarcodeLogic() {
        System.out.println("generateBarcodeLogic");
        ClassicBarcodeLogicHandler logic = null;
        String msg = "";
        Code128LogicImpl instance = new Code128LogicImpl();
        instance.generateBarcodeLogic(logic, msg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/
}
