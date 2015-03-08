/*
 * Copyright 2002-2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.upcean;

import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.MockClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.NullClassicBarcodeLogicHandler;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test class for the UPC-E implementation.
 *
 * @author Jeremias Maerki
 * @version $Id$
 */
public class UPCELogicImplTest {

    @Test
    public void testIllegalArguments() throws Exception {

        //Test invalid characters in message
        try {
            UPCELogicImpl logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123èöö2");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }

        //Test less than 12 characters
        try {
            UPCELogicImpl logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123");
            fail("Expected an exception complaining about invalid message length");
        } catch (IllegalArgumentException iae) {
            //must fail
        }

        //Test more than 12 characters
        try {
            UPCELogicImpl logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(),
                    "123456789012344567890");
            fail("Expected an exception complaining about invalid message length");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }

    private static final String[][] COMPACTION_TESTS = {
        {"01278907", "012000007897"},
        {"01278916", "012100007896"},
        {"01278925", "012200007895"},
        {"01238935", "012300000895"},
        {"01248934", "012400000894"},
        {"01258933", "012500000893"},
        {"01268932", "012600000892"},
        {"01278931", "012700000891"},
        {"01288930", "012800000890"},
        {"01298939", "012900000899"},
        {"01291944", "012910000094"},
        {"01291155", "012911000055"},
        {"01291162", "012911000062"},
        {"01291179", "012911000079"},
        {"01291186", "012911000086"},
        {"00102546", "001020000056"},
        {"01234133", "012300000413"}};

    @Test
    public void testMessageCompaction() throws Exception {
        UPCELogicImpl impl = new UPCELogicImpl(ChecksumMode.CP_AUTO);
        for (String[] testValue : COMPACTION_TESTS) {
            assertEquals(testValue[1] + " must be compacted to " + testValue[0], testValue[0], impl.compactMessage(testValue[1]));
            String nocheck = testValue[1].substring(0, 11);
            assertEquals(nocheck + " must be compacted to " + testValue[0], testValue[0], impl.compactMessage(nocheck));
        }
        final String noUPCE = "01234567890";
        assertNull(impl.compactMessage(noUPCE));
        assertNull(impl.compactMessage(noUPCE + impl.calcChecksum(noUPCE)));
        try {
            impl.compactMessage("ajsgf");
            fail("Invalid messages must fail");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
        try {
            impl.compactMessage("0000000000000000000000000");
            fail("Invalid messages must fail");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }

    @Test
    public void testMessageExpansion() throws Exception {
        UPCELogicImpl impl = new UPCELogicImpl(ChecksumMode.CP_AUTO);
        for (String[] testValue : COMPACTION_TESTS) {
            assertEquals(testValue[0] + " must be expanded to " + testValue[1], testValue[1], impl.expandMessage(testValue[0]));
            String nocheck = testValue[0].substring(0, 7);
            assertEquals(nocheck + " must be expanded to " + testValue[1], testValue[1], impl.expandMessage(nocheck));
        }
    }

    @Test
    public void testLogic() throws Exception {
        StringBuilder sb = new StringBuilder();
        UPCELogicImpl logic;
        String expected;

        logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "0425261");
        expected = "<BC>"
                + "<SBG:upc-ean-guard:null>B1W1B1</SBG>"
                + "<SBG:upc-ean-lead:0>"
                + "</SBG>"
                + "<SBG:upc-ean-group:425261>"
                + "<SBG:msg-char:4>W2B3W1B1</SBG>"
                + "<SBG:msg-char:2>W2B1W2B2</SBG>"
                + "<SBG:msg-char:5>W1B3W2B1</SBG>"
                + "<SBG:msg-char:2>W2B2W1B2</SBG>"
                + "<SBG:msg-char:6>W1B1W1B4</SBG>"
                + "<SBG:msg-char:1>W2B2W2B1</SBG>"
                + "</SBG>"
                + "<SBG:upc-ean-check:4>"
                + "</SBG>"
                + "<SBG:upc-ean-guard:null>W1B1W1B1W1B1</SBG>"
                + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

        //The same but with check mode
        sb.setLength(0);
        logic = new UPCELogicImpl(ChecksumMode.CP_CHECK);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "04252614");
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

        //The same but with UPC-A message
        sb.setLength(0);
        logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "042100005264");
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

        //The same but with UPC-A message without checksum
        sb.setLength(0);
        logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "04210000526");
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
    }
}
