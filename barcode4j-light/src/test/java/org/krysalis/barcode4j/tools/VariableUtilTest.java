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
package org.krysalis.barcode4j.tools;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mk
 */
public class VariableUtilTest {

    public VariableUtilTest() {
    }

    /**
     * Test of getExpandedMessage method, of class VariableUtil.
     */
    @Test
    public void getExpandedMessageNoPage() {
        System.out.println("getExpandedMessage");
        PageInfo page = null;
        String msg = "#page-number#";
        String expResult = "000";
        String result = VariableUtil.getExpandedMessage(page, msg);
        assertEquals(expResult, result);
    }

    @Test
    public void getExpandedMessageWithPage() {
        System.out.println("getExpandedMessage");
        PageInfo page = new PageInfo(42, "42");
        String msg = "#page-number#";
        String expResult = "42";
        String result = VariableUtil.getExpandedMessage(page, msg);
        assertEquals(expResult, result);
    }

    @Test
    public void getExpandedMessageWithPageFormat() {
        System.out.println("getExpandedMessage");
        PageInfo page = new PageInfo(42, "42");
        String msg = "Hallo Welt #page-number:0000#";
        String expResult = "Hallo Welt 0042";
        String result = VariableUtil.getExpandedMessage(page, msg);
        assertEquals(expResult, result);
    }
}
