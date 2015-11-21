/*
 * Copyright 2002-2004,2006,2008-2009 Jeremias Maerki.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This is a simple implementation of a BarcodeClassResolver.
 *
 * @version $Id$
 */
public class DefaultBarcodeClassResolver implements BarcodeClassResolver {

    private static final String INTL_2_OF_5_CLASSNAME = "org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5";
    private static final String EAN_128_CLASSNAME = "org.krysalis.barcode4j.impl.code128.EAN128";
    private Map<String, String> classes;
    private Set<String> mainIDs;

    /**
     * Main constructor.
     *
     * Already registers a default set of implementations.
     */
    public DefaultBarcodeClassResolver() {
        registerBarcodeClass("codabar", "org.krysalis.barcode4j.impl.codabar.Codabar", true);
        registerBarcodeClass("code39", "org.krysalis.barcode4j.impl.code39.Code39", true);
        registerBarcodeClass("code128", "org.krysalis.barcode4j.impl.code128.Code128", true);
        registerBarcodeClass("ean-128", EAN_128_CLASSNAME, true);
        registerBarcodeClass("ean128", EAN_128_CLASSNAME);
        registerBarcodeClass("2of5", INTL_2_OF_5_CLASSNAME);
        registerBarcodeClass("intl2of5", INTL_2_OF_5_CLASSNAME, true);
        registerBarcodeClass("interleaved2of5", INTL_2_OF_5_CLASSNAME);
        registerBarcodeClass("itf-14", "org.krysalis.barcode4j.impl.int2of5.ITF14", true);
        registerBarcodeClass("itf14", "org.krysalis.barcode4j.impl.int2of5.ITF14");
        registerBarcodeClass("ean-13", "org.krysalis.barcode4j.impl.upcean.EAN13", true);
        registerBarcodeClass("ean13", "org.krysalis.barcode4j.impl.upcean.EAN13");
        registerBarcodeClass("ean-8", "org.krysalis.barcode4j.impl.upcean.EAN8", true);
        registerBarcodeClass("ean8", "org.krysalis.barcode4j.impl.upcean.EAN8");
        registerBarcodeClass("upc-a", "org.krysalis.barcode4j.impl.upcean.UPCA", true);
        registerBarcodeClass("upca", "org.krysalis.barcode4j.impl.upcean.UPCA");
        registerBarcodeClass("upc-e", "org.krysalis.barcode4j.impl.upcean.UPCE", true);
        registerBarcodeClass("upce", "org.krysalis.barcode4j.impl.upcean.UPCE");
        registerBarcodeClass("postnet", "org.krysalis.barcode4j.impl.postnet.POSTNET", true);
        registerBarcodeClass("royal-mail-cbc",
                "org.krysalis.barcode4j.impl.fourstate.RoyalMailCBC", true);
        registerBarcodeClass("usps4cb",
                "org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMail", true);
        registerBarcodeClass("pdf417", "org.krysalis.barcode4j.impl.pdf417.PDF417", true);
        registerBarcodeClass("datamatrix",
                "org.krysalis.barcode4j.impl.datamatrix.DataMatrix", true);
    }

    /**
     * Registers a barcode implementation.
     * @param id short name to use as a key
     * @param classname fully qualified classname
     */
    public final void registerBarcodeClass(String id, String classname) {
        registerBarcodeClass(id, classname, false);
    }

    /**
     * Registers a barcode implementation.
     * @param id short name to use as a key
     * @param classname fully qualified classname
     * @param mainID indicates whether the name is the main name for the barcode
     */
    public final synchronized void registerBarcodeClass(String id, String classname, boolean mainID) {
        if (this.classes == null) {
            this.classes = new HashMap<String, String>();
            this.mainIDs = new HashSet<String>();
        }
        this.classes.put(id.toLowerCase(), classname);
        if (mainID) {
            this.mainIDs.add(id);
        }
    }

    @Override
    public Class<?> resolve(String name) throws ClassNotFoundException {
        String clazz = null;
        if (this.classes != null) {
            clazz = (String)this.classes.get(name.toLowerCase());
        }
        if (clazz == null) {
            clazz = name;
        }
        return Class.forName(clazz);
    }

    /**
     * Formerly used to get Barcode-Bean-Implementations.
     * 
     * @param name Symbologiename
     * @return resolved class
     * @throws ClassNotFoundException if no Implementation found
     * @deprecated as of version 2.1.2, replaced by {@link BarcodeGeneratorProvider#getBarcodeGenerator(java.lang.String)}
     */
    @Deprecated
    @Override
    public Class<?> resolveBean(String name) throws ClassNotFoundException {
        String clazz = null;
        if (this.classes != null) {
            clazz = (String)this.classes.get(name.toLowerCase());
        }
        if (clazz == null) {
            clazz = name;
        }
        return Class.forName(clazz + "Bean");
    }

    @Override
    public Collection<String> getBarcodeNames() {
        return Collections.unmodifiableCollection(this.mainIDs);
    }
}
