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
package org.krysalis.barcode4j.impl.datamatrix.encoder;

import java.util.Arrays;
import java.util.Collections;
import static org.krysalis.barcode4j.impl.datamatrix.encoder.Encodation.*;
import static org.krysalis.barcode4j.tools.CheckUtil.*;

/**
 *
 * @author mk
 */
public class LookAhead {

    private LookAhead() {
        // hide default public constructor
    }

    private static int ix(Encodation enc) {
        return enc.ordinal();
    }

    public static Encodation lookAheadTest(String msg, int startpos, Encodation currentMode) {
        if (startpos >= msg.length()) {
            return currentMode;
        }

        float[] charCounts = stepJ(currentMode);

        int charsProcessed = 0;
        while (true) {
            //step K
            if ((startpos + charsProcessed) == msg.length()) {
                int min = Integer.MAX_VALUE;
                final byte[] mins = new byte[Encodation.values().length];
                final int[] intCharCounts = new int[Encodation.values().length];
                min = findMinimums(charCounts, intCharCounts, min, mins);
                final int minCount = getMinimumCount(mins);

                if (intCharCounts[ix(ASCII_ENCODATION)] == min) {
                    return ASCII_ENCODATION;
                } else if (minCount == 1 && mins[ix(BASE256_ENCODATION)] > 0) {
                    return BASE256_ENCODATION;
                } else if (minCount == 1 && mins[ix(EDIFACT_ENCODATION)] > 0) {
                    return EDIFACT_ENCODATION;
                } else if (minCount == 1 && mins[ix(TEXT_ENCODATION)] > 0) {
                    return TEXT_ENCODATION;
                } else if (minCount == 1 && mins[ix(X12_ENCODATION)] > 0) {
                    return X12_ENCODATION;
                } else {
                    return C40_ENCODATION;
                }
            }

            final char c = msg.charAt(startpos + charsProcessed);
            charsProcessed++;

            stepL(c, charCounts);

            stepM(c, charCounts);

            stepN(c, charCounts);

            stepO(c, charCounts);

            stepP(c, charCounts);

            stepQ(c, charCounts);

            //step R
            if (charsProcessed >= 4) {
                final int min = Integer.MAX_VALUE;
                final int[] intCharCounts = new int[6];
                final byte[] mins = new byte[6];
                findMinimums(charCounts, intCharCounts, min, mins);
                final int minCount = getMinimumCount(mins);

                if (isAsciiAppropriate(intCharCounts)) {
                    return ASCII_ENCODATION;
                } else if (isBase256Appropriate(intCharCounts, mins)) {
                    return BASE256_ENCODATION;
                } else if (minCount == 1 && mins[ix(EDIFACT_ENCODATION)] > 0) {
                    return EDIFACT_ENCODATION;
                } else if (minCount == 1 && mins[ix(TEXT_ENCODATION)] > 0) {
                    return TEXT_ENCODATION;
                } else if (minCount == 1 && mins[ix(X12_ENCODATION)] > 0) {
                    return X12_ENCODATION;
                } else if (isC40Appropriate(intCharCounts)) {
                    if (intCharCounts[ix(C40_ENCODATION)] < intCharCounts[ix(X12_ENCODATION)]) {
                        return C40_ENCODATION;
                    } else if (intCharCounts[ix(C40_ENCODATION)] == intCharCounts[ix(X12_ENCODATION)]) {
                        int p = startpos + charsProcessed + 1;
                        while (p < msg.length()) {
                            final char tc = msg.charAt(p);
                            if (isX12TermSep(tc)) {
                                return X12_ENCODATION;
                            } else if (!isNativeX12(tc)) {
                                break;
                            }
                            p++;
                        }
                        return C40_ENCODATION;
                    }
                }
            }
        }
    }

    private static void stepQ(final char c, float[] charCounts) {
        // step Q
        if (isSpecialB256(c)) {
            charCounts[ix(BASE256_ENCODATION)] += 4;
        } else {
            charCounts[ix(BASE256_ENCODATION)] += 1;
        }
    }

    private static void stepP(final char c, float[] charCounts) {
        //step P
        if (isNativeEDIFACT(c)) {
            charCounts[ix(EDIFACT_ENCODATION)] += 3f / 4f;
        } else if (isExtendedASCII(c)) {
            charCounts[ix(EDIFACT_ENCODATION)] += 17f / 4f;
        } else {
            charCounts[ix(EDIFACT_ENCODATION)] += 13f / 4f;
        }
    }

    private static void stepO(final char c, float[] charCounts) {
        //step O
        if (isNativeX12(c)) {
            charCounts[ix(X12_ENCODATION)] += 2f / 3f;
        } else if (isExtendedASCII(c)) {
            charCounts[ix(X12_ENCODATION)] += 13f / 3f;
        } else {
            charCounts[ix(X12_ENCODATION)] += 10f / 3f;
        }
    }

    private static void stepN(final char c, float[] charCounts) {
        //step N
        if (isNativeText(c)) {
            charCounts[ix(TEXT_ENCODATION)] += 2f / 3f;
        } else if (isExtendedASCII(c)) {
            charCounts[ix(TEXT_ENCODATION)] += 8f / 3f;
        } else {
            charCounts[ix(TEXT_ENCODATION)] += 4f / 3f;
        }
    }

    private static void stepM(final char c, float[] charCounts) {
        //step M
        if (isNativeC40(c)) {
            charCounts[ix(C40_ENCODATION)] += 2f / 3f;
        } else if (isExtendedASCII(c)) {
            charCounts[ix(C40_ENCODATION)] += 8f / 3f;
        } else {
            charCounts[ix(C40_ENCODATION)] += 4f / 3f;
        }
    }

    private static void stepL(final char c, float[] charCounts) {
        if (isDigit(c)) {
            charCounts[ix(ASCII_ENCODATION)] += 0.5;
        } else if (isExtendedASCII(c)) {
            charCounts[ix(ASCII_ENCODATION)] = (int) Math.ceil(charCounts[ix(ASCII_ENCODATION)]);
            charCounts[ix(ASCII_ENCODATION)] += 2;
        } else {
            charCounts[ix(ASCII_ENCODATION)] = (int) Math.ceil(charCounts[ix(ASCII_ENCODATION)]);
            charCounts[ix(ASCII_ENCODATION)] += 1;
        }
    }

    private static boolean isC40Appropriate(final int[] intCharCounts) {
        final int c40CharCount = intCharCounts[ix(C40_ENCODATION)] + 1;
        final Integer[] args = new Integer[]{intCharCounts[ix(ASCII_ENCODATION)], intCharCounts[ix(BASE256_ENCODATION)], intCharCounts[ix(EDIFACT_ENCODATION)], intCharCounts[ix(TEXT_ENCODATION)]};
        return c40CharCount < Collections.min(Arrays.asList(args));
    }

    private static boolean isBase256Appropriate(final int[] intCharCounts, final byte[] mins) {
        return intCharCounts[ix(BASE256_ENCODATION)] + 1 <= intCharCounts[ix(ASCII_ENCODATION)]
                || (mins[ix(C40_ENCODATION)]
                + mins[ix(TEXT_ENCODATION)]
                + mins[ix(X12_ENCODATION)]
                + mins[ix(EDIFACT_ENCODATION)]) == 0;
    }

    private static boolean isAsciiAppropriate(final int[] intCharCounts) {
        final int asciiCharCount = intCharCounts[ix(ASCII_ENCODATION)] + 1;
        final Integer[] args = new Integer[]{intCharCounts[ix(BASE256_ENCODATION)], intCharCounts[ix(C40_ENCODATION)], intCharCounts[ix(TEXT_ENCODATION)], intCharCounts[ix(X12_ENCODATION)], intCharCounts[ix(EDIFACT_ENCODATION)]};
        return asciiCharCount <= Collections.min(Arrays.asList(args));
    }

    private static float[] stepJ(Encodation currentMode) {
        final float[] charCounts;
        //step J
        if (currentMode == Encodation.ASCII_ENCODATION) {
            charCounts = new float[]{0, 1, 1, 1, 1, 1.25f};
        } else {
            charCounts = new float[]{1, 2, 2, 2, 2, 2.25f};
            charCounts[ix(currentMode)] = 0;
        }
        return charCounts;
    }

    private static int findMinimums(float[] charCounts, int[] intCharCounts,
            int min, byte[] mins) {
        Arrays.fill(mins, (byte) 0);
        for (int i = 0; i < 6; i++) {
            intCharCounts[i] = (int) Math.ceil(charCounts[i]);
            final int current = intCharCounts[i];
            if (min > current) {
                min = current;
                Arrays.fill(mins, (byte) 0);
            }
            if (min == current) {
                mins[i]++;
            }
        }
        return min;
    }

    private static int getMinimumCount(byte[] mins) {
        int minCount = 0;
        for (int i = 0; i < 6; i++) {
            minCount += mins[i];
        }
        return minCount;
    }

    static boolean isSpace(char ch) {
        return ch == ' ';
    }

    static boolean isNativeC40(char ch) {
        return isSpace(ch)
                || isDigit(ch)
                || isUpperAtoZ(ch);
    }

    static boolean isNativeText(char ch) {
        return isSpace(ch)
                || isDigit(ch)
                || isLowerAtoZ(ch);
    }

    static boolean isNativeX12(char ch) {
        return isX12TermSep(ch)
                || isSpace(ch)
                || isDigit(ch)
                || isUpperAtoZ(ch);
    }

    static boolean isX12TermSep(char ch) {
        return (ch == '\n')
                || (ch == '*')
                || (ch == '>');
    }

    static boolean isNativeEDIFACT(char ch) {
        return intervallContains(32, 94, ch);
    }

    static boolean isSpecialB256(char ch) {
        return false; //TODO NOT IMPLEMENTED YET!!!
    }

    static void throwIllegalCharacter(char c) {
        throw new IllegalArgumentException(String.format("Illegal character: %s (0x%04X)", c, (int) c));
    }
}
