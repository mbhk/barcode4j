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
package org.krysalis.barcode4j.impl.pdf417;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mk
 */
public class ErrorCorrectionLevelTest {

    /**
     * Test of fromInt method, of class ErrorCorrectionLevel.
     */
    @Test
    public void testFromInt() {
        System.out.println("fromInt");
        assertEquals(ErrorCorrectionLevel.DEFAULT_ERROR_CORRECTION_LEVEL, ErrorCorrectionLevel.fromInt(0));
        assertEquals(ErrorCorrectionLevel.LEVEL_8, ErrorCorrectionLevel.fromInt(8));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromIntFail() {
        System.out.println("fromInt");
        assertEquals(ErrorCorrectionLevel.DEFAULT_ERROR_CORRECTION_LEVEL, ErrorCorrectionLevel.fromInt(9));
    }
}
