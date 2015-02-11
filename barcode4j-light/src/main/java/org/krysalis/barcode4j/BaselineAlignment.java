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
 * Enumeration for the alignment of bars when the heights are not uniform.
 *
 * @author Chris Dolphy
 * @author mk
 * @version 1.2
 */
public enum BaselineAlignment {

    /**
     * The bars are aligned to be even along the top.
     */
    ALIGN_TOP("top"),
    /**
     * The bars are aligned to be even along the bottom.
     */
    ALIGN_BOTTOM("bottom");

    private final String name;

    /**
     * Creates a new BaselineAlignment instance.
     *
     * @param name the name for the instance
     */
    private BaselineAlignment(String name) {
        this.name = name;
    }

    /**
     * @return the name of the instance.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a BaselineAlignment instance by name.
     *
     * @param name the name of the instance
     * @return the requested instance
     */
    public static BaselineAlignment byName(String name) {
        for (BaselineAlignment ba : BaselineAlignment.values()) {
            if (ba.getName().equalsIgnoreCase(name)) {
                return ba;
            }
        }
        throw new IllegalArgumentException(
                "Invalid BaselineAlignment: " + name);
    }
}
