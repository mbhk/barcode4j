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

import org.krysalis.barcode4j.impl.MockClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.NullClassicBarcodeLogicHandler;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mk
 */
public class Code128LogicImplTest {

    /**
     * Test of isValidChar method, of class Code128LogicImpl.
     */
    @Test
    public void isValidChar() {
        System.out.println("isValidChar");
        char ch = 010;
        boolean expResult = true;
        boolean result = Code128LogicImpl.isValidChar(ch);
        assertEquals(expResult, result);
    }

    /**
     * Test of isInCodeSetA method, of class Code128LogicImpl.
     */
    @Test
    public void isInCodeSetA() {
        System.out.println("isInCodeSetA");
        char ch = 'A';
        boolean expResult = true;
        boolean result = Code128LogicImpl.isInCodeSetA(ch);
        assertEquals(expResult, result);
    }

    /**
     * Test of isInCodeSetB method, of class Code128LogicImpl.
     */
    @Test
    public void isInCodeSetB() {
        System.out.println("isInCodeSetB");
        char ch = ' ';
        boolean expResult = true;
        boolean result = Code128LogicImpl.isInCodeSetB(ch);
        assertEquals(expResult, result);
    }

    /**
     * Test of canBeInCodeSetC method, of class Code128LogicImpl.
     */
    @Test
    public void canBeInCodeSetC() {
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
    @Test
    public void symbolCharToString() {
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
    @Test
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
    @Test
    public void getEncoder() {
        System.out.println("getEncoder");
        Code128LogicImpl instance = new Code128LogicImpl();
        Code128Encoder result = instance.getEncoder();
        assertNotNull(result);
    }

    /**
     * Test of createEncodedMessage method, of class Code128LogicImpl.
     */
    @Test
    public void createEncodedMessage() {
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

    @Test
    public void logic() throws Exception {
        StringBuilder sb = new StringBuilder();
        Code128LogicImpl logic;
        String expected;

        try {
            logic = new Code128LogicImpl();
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123èöö2");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }

        logic = new Code128LogicImpl();
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "123");
        expected = "<BC>"
            + "<SBG:msg-char:StartB>B2W1B1W2B1W4</SBG>"
            + "<SBG:msg-char:idx17>B1W2B3W2B2W1</SBG>"
            + "<SBG:msg-char:idx18>B2W2B3W2B1W1</SBG>"
            + "<SBG:msg-char:idx19>B2W2B1W1B3W2</SBG>"
            + "<SBG:msg-char:idx8>B1W3B2W2B1W2</SBG>"
            + "<SBG:stop-char:null>B2W3B3W1B1W1B2</SBG>"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
    }

    @Test
    public void nonPrintableAscii() throws Exception {
        StringBuilder sb = new StringBuilder();
        String expected;
        Code128LogicImpl logic = new Code128LogicImpl();
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb, false, true),
                "AA\rBB\tCC");
        expected = "<BC:AA BB CC>"
            + "<SBG:msg-char:StartA></SBG>"
            + "<SBG:msg-char:idx33></SBG>"
            + "<SBG:msg-char:idx33></SBG>"
            + "<SBG:msg-char:idx77></SBG>"
            + "<SBG:msg-char:idx34></SBG>"
            + "<SBG:msg-char:idx34></SBG>"
            + "<SBG:msg-char:idx73></SBG>"
            + "<SBG:msg-char:idx35></SBG>"
            + "<SBG:msg-char:idx35></SBG>"
            + "<SBG:msg-char:idx54></SBG>"
            + "<SBG:stop-char:null></SBG>"
            + "</BC>";
        assertEquals(expected, sb.toString());
    }

    @Test
    public void bug942246() throws Exception {
        Code128LogicImpl logic = new Code128LogicImpl();
        logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(),
            "\u00f1020456789012341837100\u00f13101000200");
        //expect no failure
    }
}
