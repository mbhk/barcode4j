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
package org.krysalis.barcode4j.tools;

/**
 *
 * @author mk
 */
public class CheckUtil {

    private CheckUtil() {
        // hide public default constructor
    }

    /**
     * Checks if n is in range low..high.
     *
     * @param low lower bound (inclusive)
     * @param high upper bound (inclusive)
     * @param n checked value
     * @return true if n is in range
     */
    public static boolean intervallContains(int low, int high, int n) {
        return low <= n && n <= high;
    }

    /**
     * Checks if n is a ASCII-Digit.
     *
     * @param ch Character to check
     * @return true if n is digit
     */
    public static boolean isDigit(char ch) {
        return intervallContains('0', '9', ch);
    }

    public static boolean isASCII7(char ch) {
        return intervallContains(0, 127, ch);
    }

    public static boolean isExtendedASCII(char ch) {
        return intervallContains(128, 255, ch);
    }

    public static boolean isUpperAtoZ(char ch) {
        return intervallContains('A', 'Z', ch);
    }

    public static boolean isLowerAtoZ(char ch) {
        return intervallContains('a', 'z', ch);
    }
}
