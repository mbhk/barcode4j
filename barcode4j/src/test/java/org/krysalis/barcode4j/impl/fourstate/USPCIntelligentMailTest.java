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
package org.krysalis.barcode4j.impl.fourstate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.mbhk.barcode4j.Configuration;

/**
 *
 * @author mk
 */
public class USPCIntelligentMailTest {
    
    @Test
    public void testDefaultsInXML() throws Exception {
        USPSIntelligentMailBean refBean = new USPSIntelligentMailBean();
        
        USPSIntelligentMail gen = new USPSIntelligentMail();
        Configuration cfg = new Configuration("usps4cb");
        gen.configure(cfg);
        USPSIntelligentMailBean xmlBean = gen.getUSPSIntelligentMailBean();
        assertEquals(refBean.getAscenderHeight(), xmlBean.getAscenderHeight(), 0.01);
        assertEquals(refBean.getBarHeight(), xmlBean.getBarHeight(), 0.01);
        assertEquals(refBean.getFontSize(), xmlBean.getFontSize(), 0.01);
        assertEquals(refBean.getHeight(), xmlBean.getHeight(), 0.01);
        assertEquals(refBean.getHumanReadableHeight(), xmlBean.getHumanReadableHeight(), 0.01);
        assertEquals(refBean.getIntercharGapWidth(), xmlBean.getIntercharGapWidth(), 0.01);
        assertEquals(refBean.getModuleWidth(), xmlBean.getModuleWidth(), 0.01);
        assertEquals(refBean.getQuietZone(), xmlBean.getQuietZone(), 0.01);
        assertEquals(refBean.getTrackHeight(), xmlBean.getTrackHeight(), 0.01);
        assertEquals(refBean.getVerticalQuietZone(), xmlBean.getVerticalQuietZone(), 0.01);
        assertEquals(refBean.hasQuietZone(), xmlBean.hasQuietZone());
        assertEquals(refBean.getChecksumMode(), xmlBean.getChecksumMode());
        assertEquals(refBean.getMsgPosition(), xmlBean.getMsgPosition());
        assertEquals(refBean.getPattern(), xmlBean.getPattern());
    }
}
