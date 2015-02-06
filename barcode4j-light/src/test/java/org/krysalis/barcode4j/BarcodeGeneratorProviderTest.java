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

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author mk
 */
public class BarcodeGeneratorProviderTest extends TestCase {
    
    public BarcodeGeneratorProviderTest(String testName) {
        super(testName);
    }

    /**
     * Test of getInstance method, of class BarcodeGeneratorProvider.
     */
    public void testGetInstance() {
        System.out.println("getInstance");
        BarcodeGeneratorProvider result = BarcodeGeneratorProvider.getInstance();
        assertNotNull(result);
        BarcodeGeneratorProvider result2 = BarcodeGeneratorProvider.getInstance();
        assertEquals(result, result2);
    }

    /**
     * Test of getAvailableBarcodeGenerators method, of class BarcodeGeneratorProvider.
     */
    public void testGetAvailableBarcodeGenerators() {
        System.out.println("getAvailableBarcodeGenerators");
        BarcodeGeneratorProvider instance = BarcodeGeneratorProvider.getInstance();
        Collection<String> result = instance.getAvailableBarcodeGenerators();
        assertNotNull(result);
        assertTrue("some generators loadable", result.size() > 0);
    }

    /**
     * Test of getBarcodeGenerator method, of class BarcodeGeneratorProvider.
     * @throws java.lang.Exception
     */
    public void testGetBarcodeGenerator() throws Exception {
        System.out.println("getBarcodeGenerator");
        String id = "qr";
        BarcodeGeneratorProvider instance = BarcodeGeneratorProvider.getInstance();
        BarcodeGenerator result = instance.getBarcodeGenerator(id);
        assertNotNull(result);
        BarcodeGenerator result2 = instance.getBarcodeGenerator(id);
        assertNotSame("must be new instance", result2, result);
    }
    
    public void testBadBarcodeGenerator() {
        BarcodeGeneratorProvider instance = BarcodeGeneratorProvider.getInstance();
        try {
            BarcodeGenerator bad = instance.getBarcodeGenerator("foobar");
            fail("must throw exception");
        } catch (BarcodeException ex) {
            Logger.getLogger(BarcodeGeneratorProviderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}