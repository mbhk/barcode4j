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

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mk
 */
public class ChecksumModeTest {

    /**
     * Test of getName method, of class ChecksumMode.
     */
    @Test
    public void getName() {
        System.out.println("getName");
        assertEquals("auto", ChecksumMode.CP_AUTO.getName());
    }

    /**
     * Test of byName method, of class ChecksumMode.
     */
    @Test
    public void byName() {
        System.out.println("byName");
        assertEquals(ChecksumMode.CP_IGNORE, ChecksumMode.byName("ignore"));

        try {
            ChecksumMode.byName("foo");
            fail("foo is no ChecksumMode");
        } catch (IllegalArgumentException e) {
        }
    }
}
