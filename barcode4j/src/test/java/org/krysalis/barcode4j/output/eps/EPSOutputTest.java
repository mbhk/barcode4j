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
package org.krysalis.barcode4j.output.eps;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;

import com.github.mbhk.barcode4j.Configuration;

/**
 * Test class for basic EPS output functionality.
 * 
 * @author Jeremias Maerki
 * @version $Id$
 */
public class EPSOutputTest {

    @Test
    public void testEPS() throws Exception {
        Configuration cfg = new Configuration("cfg");
        cfg.addChild(new Configuration("intl2of5"));

        BarcodeUtil util = BarcodeUtil.getInstance();
        BarcodeGenerator gen = util.createBarcodeGenerator(cfg);
        
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        EPSCanvasProvider provider = new EPSCanvasProvider(baout, 0); 

        //Create Barcode and render it to EPS
        gen.generateBarcode(provider, "123");
        provider.finish();
        
        assertTrue(baout.size() > 0);
    }
}
