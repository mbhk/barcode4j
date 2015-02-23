/*
 * Copyright 2007 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.datamatrix;

/**
 * Enumeration for DataMatrix symbol shape hint.
 *
 * It can be used to force square or rectangular symbols.
 *
 * @version 1.3
 */
public enum SymbolShapeHint {

    /**
     * The human-readable part is suppressed.
     */
    FORCE_NONE("force-none"),
    /**
     * The human-readable part is placed at the top of the barcode.
     */
    FORCE_SQUARE("force-square"),
    /**
     * The human-readable part is placed at the bottom of the barcode.
     */
    FORCE_RECTANGLE("force-rectangle");

    private final String name;

    /**
     * Creates a new SymbolShapeHint instance.
     *
     * @param name the name for the instance
     */
    private SymbolShapeHint(String name) {
        this.name = name;
    }

    /**
     * @return the name of the instance.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a SymbolShapeHint instance by name.
     *
     * @param name the name of the instance
     * @return the requested instance
     */
    public static SymbolShapeHint byName(String name) {
        for (final SymbolShapeHint ref : SymbolShapeHint.values()) {
            if (ref.name.equalsIgnoreCase(name)) {
                return ref;
            }
        }
        throw new IllegalArgumentException("Invalid SymbolShapeHint: " + name);
    }

    @Override
    public String toString() {
        return getName();
    }
}
