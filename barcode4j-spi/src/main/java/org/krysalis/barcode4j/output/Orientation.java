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
package org.krysalis.barcode4j.output;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The Orientation (0, 90, 180 or 270).
 *
 * @author mk
 */
public enum Orientation {

    ZERO(Orientation.STEP_DEGREE * 0),
    NINETY(Orientation.STEP_DEGREE * 1),
    ONEHUNDRED_EIGHTY(Orientation.STEP_DEGREE * 2),
    TWOHUNDRED_SEVENTY(Orientation.STEP_DEGREE * 3);

    private static final int STEP_DEGREE = 90;
    private static final int SWAP_DEGREE = STEP_DEGREE * 2;
    private static final int MAX_DEGREES = STEP_DEGREE * 4;

    private final int rotation;

    private Orientation(int rotation) {
        this.rotation = rotation;
    }

    /**
     * Degree value of this Orientation.
     * 
     * @return orientation in degrees
     */
    public int getDegrees() {
        return rotation;
    }

    /**
     * Helper for interpreting Dimensions of Barcodes using this Orientation.
     * 
     * @return true if height has to be interpreted as width and vice versa
     */
    public boolean isSwitched() {
        return getDegrees() % SWAP_DEGREE != 0;
    }

    /**
     * Gets orientation after normalizing to range 0..359.
     *
     * @param value orientation in degrees multiplies of {0, 90, 180, 270}
     * @return corresponding Orientation
     */
    public static Orientation fromInt(int value) {
        return strictFromInt(normalize(value));
    }

    /**
     * Gets orientation after normalizing to range 0..359 and finding the
     * nearest neighbour.
     * <p>
     * 89 will be interpreted as 90 whereas 45 will be interpreted as 0.
     *
     * @param value orientation in degrees
     * @return corresponding Orientation
     */
    public static Orientation lenientFromInt(int value) {
        return strictFromInt(toNearest(normalize(value)));
    }

    /**
     * Gets orientation by strict match.
     *
     * @param value orientation in degrees {0, 90, 180, 270}
     * @return corresponding Orientation
     */
    public static Orientation strictFromInt(int value) {
        for (final Orientation orientation : Orientation.values()) {
            if (orientation.getDegrees() == value) {
                return orientation;
            }
        }
        throw new IllegalArgumentException(
                "orientation must be in {0, 90, 180, 270}.");
    }

    private static int normalize(int value) {
        int res = value;
        if (res < 0) {
            res = res + (1 + (Math.abs(res) % MAX_DEGREES)) * MAX_DEGREES;
        }
        return res % MAX_DEGREES;
    }

    private static int toNearest(int value) {
        final int normalized = normalize(value);
        final BigDecimal ninety = BigDecimal.valueOf(STEP_DEGREE);
        final int nearest = BigDecimal.valueOf(normalized).divide(ninety, 0, RoundingMode.HALF_UP).multiply(ninety).intValue();
        return nearest >= MAX_DEGREES ? nearest - MAX_DEGREES : nearest;
    }
}
