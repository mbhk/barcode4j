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
 * Enumeration type for checksum policy.
 *
 * @author Jeremias Maerki
 * @version 1.3
 */
public enum ChecksumMode {

    /**
     * Chooses the barcode's default checksum behaviour.
     */
    CP_AUTO("auto"),
    /**
     * Doesn't check nor add a checksum.
     */
    CP_IGNORE("ignore"),
    /**
     * Adds the necessary checksum to the message to be encoded.
     */
    CP_ADD("add"),
    /**
     * Requires the check character to be present in the message.
     *
     * It will be checked.
     */
    CP_CHECK("check");

    private final String name;

    /**
     * Creates a new ChecksumMode instance.
     *
     * @param name the name of the ChecksumMode
     */
    private ChecksumMode(String name) {
        this.name = name;
    }

    /**
     * Gets the associated name.
     *
     * @return the name of the instance.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a ChecksumMode instance by name.
     *
     * @param name the name of the ChecksumMode
     * @return the requested instance
     */
    public static ChecksumMode byName(String name) {
        for (final ChecksumMode m : ChecksumMode.values()) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Invalid ChecksumMode: " + name);
    }
}
