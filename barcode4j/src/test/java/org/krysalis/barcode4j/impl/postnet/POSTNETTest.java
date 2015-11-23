/*
 * Copyright 2003,2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.postnet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.mbhk.barcode4j.DefaultConfiguration;

/**
 * Test class for the POSTNET implementation.
 *
 * @author Chris Dolphy
 * @version $Id$
 */
public class POSTNETTest {

    @Test
    public void testIllegalArguments() throws Exception {
        try {
            POSTNET impl = new POSTNET();
            impl.generateBarcode(null, null);
            fail("Expected an NPE");
        } catch (NullPointerException npe) {
            assertNotNull("Error message is empty", npe.getMessage());
        }
    }

    @Test
    public void testDefaultsInXML() throws Exception {
        POSTNETBean refBean = new POSTNETBean();

        POSTNET gen = new POSTNET();
        DefaultConfiguration cfg = new DefaultConfiguration("postnet");
        gen.configure(cfg);
        POSTNETBean xmlBean = gen.getPOSTNETBean();
        assertEquals(refBean.getBarHeight(), xmlBean.getBarHeight(), 0.01);
        assertEquals(refBean.getBaselinePosition(), xmlBean.getBaselinePosition());
        assertEquals(refBean.getChecksumMode(), xmlBean.getChecksumMode());
        assertEquals(refBean.isDisplayChecksum(), xmlBean.isDisplayChecksum());
        assertEquals(refBean.getFontSize(), xmlBean.getFontSize(), 0.01);
        assertEquals(refBean.getHeight(), xmlBean.getHeight(), 0.01);
        assertEquals(refBean.getHumanReadableHeight(), xmlBean.getHumanReadableHeight(), 0.01);
        assertEquals(refBean.getIntercharGapWidth(), xmlBean.getIntercharGapWidth(), 0.01);
        assertEquals(refBean.getModuleWidth(), xmlBean.getModuleWidth(), 0.01);
        assertEquals(refBean.getQuietZone(), xmlBean.getQuietZone(), 0.01);
        assertEquals(refBean.getShortBarHeight(), xmlBean.getShortBarHeight(), 0.01);
        assertEquals(refBean.getVerticalQuietZone(), xmlBean.getVerticalQuietZone(), 0.01);
        assertEquals(refBean.hasQuietZone(), xmlBean.hasQuietZone());
        assertEquals(refBean.getChecksumMode(), xmlBean.getChecksumMode());
        assertEquals(refBean.getMsgPosition(), xmlBean.getMsgPosition());
        assertEquals(refBean.getPattern(), xmlBean.getPattern());
    }
}
