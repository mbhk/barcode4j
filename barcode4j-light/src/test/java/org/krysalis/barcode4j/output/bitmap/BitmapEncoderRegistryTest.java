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
package org.krysalis.barcode4j.output.bitmap;

import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author mk
 */
public class BitmapEncoderRegistryTest extends TestCase {

    /**
     * Test of register method, of class BitmapEncoderRegistry.
     */
    public void testRegister() {
        System.out.println("register");
        String classname = ImageIOBitmapEncoder.class.getName();
        int priority = 42;
        BitmapEncoderRegistry.register(classname, priority);
        assertNotNull("image/png is supported by given provider",
                BitmapEncoderRegistry.getInstance("image/png"));

        try {
            BitmapEncoderRegistry.register("foobar." + classname, 1);
            fail("registering should be not possible");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test of supports method, of class BitmapEncoderRegistry.
     */
    public void testSupports_BitmapEncoder_String() {
        System.out.println("supports");
        String mime = "image/png";
        BitmapEncoder encoder = BitmapEncoderRegistry.getInstance(mime);
        boolean expResult = true;
        boolean result = BitmapEncoderRegistry.supports(encoder, mime);
        assertEquals(expResult, result);
    }

    /**
     * Test of supports method, of class BitmapEncoderRegistry.
     */
    public void testSupports_String() {
        System.out.println("supports");
        String mime = "image/jpeg";
        boolean expResult = true;
        boolean result = BitmapEncoderRegistry.supports(mime);
        assertEquals(expResult, result);
        assertEquals(!expResult, BitmapEncoderRegistry.supports("foo/bar"));
    }

    /**
     * Test of getInstance method, of class BitmapEncoderRegistry.
     */
    public void testGetInstance() {
        System.out.println("getInstance");
        String mime = "image/gif";
        BitmapEncoder result = BitmapEncoderRegistry.getInstance(mime);
        assertNotNull(result);

        try {
            BitmapEncoderRegistry.getInstance("foo/app");
            fail("no instance should beavailable");
        } catch (UnsupportedOperationException e) {
        }
    }

    /**
     * Test of getSupportedMIMETypes method, of class BitmapEncoderRegistry.
     */
    public void testGetSupportedMIMETypes() {
        System.out.println("getSupportedMIMETypes");
        boolean expResult = true;
        Set<String> result = BitmapEncoderRegistry.getSupportedMIMETypes();
        assertEquals(expResult, result.contains("image/jpeg"));
        assertEquals(!expResult, result.contains("image/jpg"));
    }
}
