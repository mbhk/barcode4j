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
package org.krysalis.barcode4j.impl.pdf417;

/**
 *
 * @author mk
 */
public enum ErrorCorrectionLevel {
    DEFAULT_ERROR_CORRECTION_LEVEL(0),
    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(3),
    LEVEL_4(4),
    LEVEL_5(5),
    LEVEL_6(6),
    LEVEL_7(7),
    LEVEL_8(8);
    
    private final int level;
    
    private ErrorCorrectionLevel(int i) {
        this.level = i;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public static ErrorCorrectionLevel fromInt(int i) {
        for (final ErrorCorrectionLevel l : ErrorCorrectionLevel.values()) {
            if (l.getLevel() == i) {
                return l;
            }
        }
        throw new IllegalArgumentException("not a valid ErrorCorrectionLevel: " + i);
    }
}
