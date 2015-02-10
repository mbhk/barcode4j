/*
 * Copyright 2008 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.code128;

/**
 * Constants for Code128.
 *
 * @version 1.2
 */
public enum Code128Constants {

    /**
     * Enables the codeset A
     */
    CODESET_A(1),
    /**
     * Enables the codeset B
     */
    CODESET_B(2),
    /**
     * Enables the codeset C
     */
    CODESET_C(4),
    /**
     * Enables the codesets A&amp;B
     */
    CODESET_AB(CODESET_A.value | CODESET_B.value),
    /**
     * Enables the codesets A&amp;C
     */
    CODESET_AC(CODESET_A.value | CODESET_C.value),
    /**
     * Enables the codesets B&amp;C
     */
    CODESET_BC(CODESET_B.value | CODESET_C.value),
    /**
     * Enables all codesets
     */
    CODESET_ALL(CODESET_A.value | CODESET_B.value | CODESET_C.value);

    private final int value;

    private Code128Constants(int value) {
        this.value = value;
    }

    public static Code128Constants fromInt(int i) {
        Code128Constants res = null;
        for (final Code128Constants v : Code128Constants.values()) {
            if (v.value == i) {
                res = v;
            }
        }
        if (res == null) {
            throw new IllegalArgumentException("Can not convert " + i + " to " + Code128Constants.class.getName());
        }
        return res;
    }

    public static Code128Constants and(Code128Constants a, Code128Constants b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return fromInt(a.value | b.value);
    }

    public int getValue() {
        return value;
    }
}
