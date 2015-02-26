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
     * @param n Character to check
     * @return true if n is digit
     */
    public static boolean isDigit(char n) {
        return intervallContains('0', '9', n);
    }
}
