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
package org.krysalis.barcode4j;

/**
 * Some constants for Barcode4J.
 *
 * @author Jeremias Maerki
 * @version 1.2
 */
public enum BarcodeConstants {

    /**
     * The namespace used when embedding barcode XML inside other XML dialects
     * such as XSL-FO.
     */
    NAMESPACE("http://barcode4j.krysalis.org/ns"),

    /**
     * For compatibility: The Krysalis Barcode namespace valid before this project
     * was renamed.
     */
    OLD_NAMESPACE("http://www.krysalis.org/barcode/ns"),
    
    /**
     * URL to the Barcode4J website.
     */
    WEBSITE("http://barcode4j.sourceforge.net");

    private final String value;

    private BarcodeConstants(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
