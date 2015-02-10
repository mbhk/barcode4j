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

/**
 * The Orientation (0, 90, 180 or 270).
 *
 * @author mk
 */
public enum Orientation {

    ZERO(0), NINETY(90), ONEHUNDRED_EIGHTY(180), TWOHUNDRED_SEVENTY(270);

    private final int rotation;

    private Orientation(int rotation) {
        this.rotation = rotation;
    }

    public int getDegrees() {
        return rotation;
    }

    public boolean isSwitched() {
        return getDegrees() % 180 != 0;
    }

    public static Orientation fromInt(int value) {
        final int normalized = normalize(value);
        for (final Orientation orientation : Orientation.values()) {
            if (orientation.getDegrees() == normalized) {
                return orientation;
            }
        }
        throw new IllegalArgumentException(
                "Orientation must be 0, 90, 180, 270 (or convertable).");
    }

    private static int normalize(int value) {
        int res = value;
        if (res < 0) {
            res = res + (1 + (Math.abs(res) % 360)) * 360;
        }
        return res % 360;
    }
}
