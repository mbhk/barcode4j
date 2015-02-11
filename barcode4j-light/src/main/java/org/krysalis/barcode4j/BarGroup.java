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
package org.krysalis.barcode4j;

/**
 * Enumeration type for bar groups.
 *
 * @author Jeremias Maerki
 * @author mk
 * @version 1.2
 */
public enum BarGroup {

    /**
     * Bar group is represents a start character.
     */
    START_CHARACTER("start-char"),
    /**
     * Bar group is represents a stop character.
     */
    STOP_CHARACTER("stop-char"),
    /**
     * Bar group is represents a message character or a part of the message.
     */
    MSG_CHARACTER("msg-char"),
    /**
     * Bar group is represents a UPC/EAN guard.
     */
    UPC_EAN_GUARD("upc-ean-guard"),
    /**
     * Bar group is represents a UPC/EAN lead.
     */
    UPC_EAN_LEAD("upc-ean-lead"),
    /**
     * Bar group is represents a UPC/EAN character group.
     */
    UPC_EAN_GROUP("upc-ean-group"),
    /**
     * Bar group is represents a UPC/EAN check character.
     */
    UPC_EAN_CHECK("upc-ean-check"),
    /**
     * Bar group is represents a UPC/EAN supplemental.
     */
    UPC_EAN_SUPP("upc-ean-supp");

    private final String name;

    /**
     * Creates a new BarGroup instance.
     *
     * @param name name of the BarGroup
     */
    private BarGroup(String name) {
        this.name = name;
    }

    /**
     * @return the name of the instance
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a BarGroup instance by name.
     *
     * @param name the name of the desired BarGroup
     * @return the requested BarGroup instance
     */
    public static BarGroup byName(String name) {
        for (BarGroup g : BarGroup.values()) {
            if (g.getName().equalsIgnoreCase(name)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Invalid BarGroup: " + name);
    }
}
