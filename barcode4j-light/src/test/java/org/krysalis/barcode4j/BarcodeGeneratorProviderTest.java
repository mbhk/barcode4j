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

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mk
 */
public class BarcodeGeneratorProviderTest {

    /**
     * Test of getInstance method, of class BarcodeGeneratorProvider.
     */
    @Test
    public void getInstance() {
        System.out.println("getInstance");
        BarcodeGeneratorProvider result = BarcodeGeneratorProvider.getInstance();
        assertNotNull(result);
        BarcodeGeneratorProvider result2 = BarcodeGeneratorProvider.getInstance();
        assertEquals(result, result2);
    }

    /**
     * Test of getAvailableBarcodeGenerators method, of class
     * BarcodeGeneratorProvider.
     */
    @Test
    public void getAvailableBarcodeGenerators() {
        System.out.println("getAvailableBarcodeGenerators");
        BarcodeGeneratorProvider instance = BarcodeGeneratorProvider.getInstance();
        Collection<String> result = instance.getAvailableBarcodeGenerators();
        assertNotNull(result);
        assertTrue("some generators loadable", result.size() > 0);
    }

    /**
     * Test of getBarcodeGenerator method, of class BarcodeGeneratorProvider.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void getBarcodeGenerator() throws Exception {
        System.out.println("getBarcodeGenerator");
        String id = "qr";
        BarcodeGeneratorProvider instance = BarcodeGeneratorProvider.getInstance();
        BarcodeGenerator result = instance.getBarcodeGenerator(id);
        assertNotNull(result);
        BarcodeGenerator result2 = instance.getBarcodeGenerator(id);
        assertNotSame("must be new instance", result2, result);
    }

    @Test(expected = BarcodeException.class)
    public void badBarcodeGenerator() throws BarcodeException {
        BarcodeGeneratorProvider instance = BarcodeGeneratorProvider.getInstance();
        instance.getBarcodeGenerator("foobar");
    }
}
