/*
 * Copyright (C) 2007 by Edmond R&D B.V.
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

import java.util.Collection;
import java.util.Arrays;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

/**
 * Tests for the Code128 encoder.
 *
 * @author branko
 * @author mk
 * @version 1.2
 */
@RunWith(Parameterized.class)
public class Code128EncoderExtendedTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"Minimal Codeset C", "StartC|idx10", "10"},
            {"Simple Codeset C with FNC1", "StartC|FNC1|idx10", "\36110"},
            {"Simple codeset C with 2 * FNC1", "StartC|FNC1|FNC1|idx10", "\361\36110"},
            {"One digit short for code set C", "StartB|FNC1|idx17", "\3611"},
            {"Minimal code set B", "StartB|idx65", "a"},
            {"Minimal code set A", "StartA|idx64", "\000"},
            {"Long code set B",
                "StartB|idx17|idx16|idx33|idx33|idx33|idx33", "10AAAA"},
            {"Long code set A",
                "StartA|idx17|idx16|idx33|idx33|idx64", "10AA\000"},
            {"Shift to B from code set A",
                "StartA|idx33|idx64|Shift/98|idx65|idx64", "A\000a\000"},
            {"Switch to B from code set A",
                "StartA|idx65|CodeB/FNC4|idx65|idx65", "\001aa"},
            {"Switch to C from code set A",
                "StartA|idx64|CodeC/99|idx0|idx0", "\0000000"},
            {"Shift to A from code set B",
                "StartB|idx65|Shift/98|idx65|idx65", "a\001a"},
            {"Switch to A from code set B",
                "StartB|idx65|CodeA/FNC4|idx65|idx65", "a\001\001"},
            {"Switch to C from code set B",
                "StartB|idx65|CodeC/99|idx0|idx0", "a0000"},
            {"Switch to A from code set C",
                "StartC|idx0|idx0|CodeA/FNC4|idx64|idx64", "0000\000\000"},
            {"Switch to B from code set C",
                "StartC|idx0|idx0|CodeB/FNC4|idx65|idx65", "0000aa"},
            {"All codeset and shifts",
                "StartC|idx0|idx0|CodeB/FNC4|idx65|idx65|Shift/98|idx64|idx65|CodeA/FNC4|idx64|idx64|Shift/98|idx65|idx64|CodeB/FNC4|idx65|idx65|CodeC/99|idx0|idx0",
                "0000aa\000a\000\000a\000aa0000"}
        });
    }

    @Parameter(value = 0)
    public String message;
    @Parameter(value = 1)
    public String expected;
    @Parameter(value = 2)
    public String plaintext;

    @Test
    public void test() {
        assertEquals(
                message,
                expected,
                Code128LogicImpl.toString(new Code128Encoder().encode(plaintext)));
    }
}
