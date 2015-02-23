/*
 * Copyright 2006 Jeremias Maerki
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

/* $Id$ */

package org.krysalis.barcode4j.tools;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test for the TestHelper.
 */
public class TestHelperTest {

    @Test
    public void testTestHelper() throws Exception {
        assertEquals("65 66 67", TestHelper.visualize("ABC"));
        assertEquals("Hello World!", TestHelper.unvisualize(TestHelper.visualize("Hello World!")));
    }
}
