/*
 * Copyright 2006-2007 Jeremias Maerki or contributors to Barcode4J, as applicable.
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

import java.awt.Dimension;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;

import static org.krysalis.barcode4j.impl.pdf417.PDF417Constants.*;

/**
 * Top-level class for the logic part of the PDF417 implementation.
 *
 * @version 1.3
 */
public class PDF417LogicImpl {

    /**
     * Calculates the necessary number of rows as described in annex Q of ISO/IEC 15438:2001(E).
     * @param m the number of source codewords prior to the additional of the Symbol Length
     *          Descriptor and any pad codewords
     * @param k the number of error correction codewords
     * @param c the number of columns in the symbol in the data region (excluding start, stop and
     *          row indicator codewords)
     * @return the number of rows in the symbol (r)
     */
    public static int getNumberOfRows(int m, int k, int c) {
        final int r = calculateNumberOfRows(m, k, c);
        if (r > 90) {
            throw new IllegalArgumentException(
                    "The message doesn't fit in the configured symbol size."
                        + " The resultant number of rows for this barcode exceeds 90."
                        + " Please increase the number of columns or decrease the error correction"
                        + " level to reduce the number of rows.");
        }
        if (r < 3) {
            throw new IllegalArgumentException(
                    "The message is too short for the configured symbol size."
                        + " The resultant number of rows is less than 3."
                        + " Please decrease the number of columns or increase the error correction"
                        + " level to increase the number of rows.");
        }
        return r;
    }

    /**
     * Calculates the necessary number of rows as described in annex Q of ISO/IEC 15438:2001(E).
     * @param m the number of source codewords prior to the additional of the Symbol Length
     *          Descriptor and any pad codewords
     * @param k the number of error correction codewords
     * @param c the number of columns in the symbol in the data region (excluding start, stop and
     *          row indicator codewords)
     * @return the number of rows in the symbol (r)
     */
    public static int calculateNumberOfRows(int m, int k, int c) {
        int r = ((m + 1 + k) / c) + 1;
        if (c * r >= (m + 1 + k + c)) {
            r--;
        }
        return r;
    }

    /**
     * Calculates the number of pad codewords as described in 4.9.2 of ISO/IEC 15438:2001(E).
     * @param m the number of source codewords prior to the additional of the Symbol Length
     *          Descriptor and any pad codewords
     * @param k the number of error correction codewords
     * @param c the number of columns in the symbol in the data region (excluding start, stop and
     *          row indicator codewords)
     * @param r the number of rows in the symbol
     * @return the number of pad codewords
     */
    public static int getNumberOfPadCodewords(int m, int k, int c, int r) {
        final int n = c * r - k;
        if (n > m + 1) {
            return (n - m) - 1;
        } else {
            return 0;
        }
    }

    /**
     * Calculates the number of data codewords (equals the Symbol Length Descriptor).
     * @param m the number of source codewords prior to the additional of the Symbol Length
     *          Descriptor and any pad codewords
     * @param errorCorrectionLevel the error correction level (value between 0 and 8)
     * @param c the number of columns in the symbol in the data region (excluding start, stop and
     *          row indicator codewords)
     * @return the number of data codewords
     */
    public static int getNumberOfDataCodewords(int m, ErrorCorrectionLevel errorCorrectionLevel, int c) {
        final int k = PDF417ErrorCorrection.getErrorCorrectionCodewordCount(errorCorrectionLevel);
        final int r = getNumberOfRows(m, k, c);
        return c * r - k;
    }

    private static void encodeChar(int pattern, int len, ClassicBarcodeLogicHandler logic) {
        int map = 1 << len - 1;
        boolean last = (pattern & map) != 0; //Initialize to inverse of first bit
        int width = 0;
        for (int i = 0; i < len; i++) {
            final boolean black = (pattern & map) != 0;
            if (last == black) {
                width++;
            } else {
                logic.addBar(last, width);
                last = black;
                width = 1;
            }
            map >>= 1;
        }
        logic.addBar(last, width);
    }

    private static void encodeLowLevel(String fullCodewords, int c, int r,
            ErrorCorrectionLevel errorCorrectionLevel, TwoDimBarcodeLogicHandler logic) {
        int idx = 0;
        for (int y = 0; y < r; y++) {
            final int cluster = y % 3;
            logic.startRow();
            logic.startBarGroup(BarGroup.START_CHARACTER, null);
            encodeChar(START_PATTERN, 17, logic);
            logic.endBarGroup();

            int left, right;
            if (cluster == 0) {
                left = (30 * (y / 3)) + ((r - 1) / 3);
                right = (30 * (y / 3)) + (c - 1);
            } else if (cluster == 1) {
                left = (30 * (y / 3)) + (errorCorrectionLevel.getLevel() * 3) + ((r - 1) % 3);
                right = (30 * (y / 3)) + ((r - 1) / 3);
            } else {
                left = (30 * (y / 3)) + (c - 1);
                right = (30 * (y / 3)) + (errorCorrectionLevel.getLevel() * 3) + ((r - 1) % 3);
            }
            int pattern;

            logic.startBarGroup(BarGroup.MSG_CHARACTER, null);
            pattern = getCodeword(cluster, left);
            encodeChar(pattern, 17, logic);
            logic.endBarGroup();

            for (int x = 0; x < c; x++) {
                logic.startBarGroup(BarGroup.MSG_CHARACTER, null);
                pattern = getCodeword(cluster, fullCodewords.charAt(idx));
                encodeChar(pattern, 17, logic);
                logic.endBarGroup();
                idx++;
            }

            logic.startBarGroup(BarGroup.MSG_CHARACTER, null);
            pattern = getCodeword(cluster, right);
            encodeChar(pattern, 17, logic);
            logic.endBarGroup();

            logic.startBarGroup(BarGroup.STOP_CHARACTER, null);
            encodeChar(STOP_PATTERN, 18, logic);
            logic.endBarGroup();
            logic.endRow();
        }
    }

    /**
     * Generates the barcode logic.
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     * @param pdf417Bean reference to the PDF417 bean for configuration access
     */
    public static void generateBarcodeLogic(TwoDimBarcodeLogicHandler logic,
            String msg, PDF417Bean pdf417Bean) {

        final ErrorCorrectionLevel errorCorrectionLevel = pdf417Bean.getErrorCorrectionLevel();

        //1. step: High-level encoding
        final int errorCorrectionCodeWords = PDF417ErrorCorrection.getErrorCorrectionCodewordCount(
                errorCorrectionLevel);
        final String highLevel = PDF417HighLevelEncoder.encodeHighLevel(msg,
                pdf417Bean.getEncoding(), pdf417Bean.isECIEnabled());
        final int sourceCodeWords = highLevel.length();

        final Dimension dimension = determineDimensions(pdf417Bean, sourceCodeWords);

        if (dimension == null) {
            throw new IllegalArgumentException(
                    "Unable to fit message in columns");
        }

        final int rows = dimension.height;
        final int cols = dimension.width;
        final int pad = getNumberOfPadCodewords(sourceCodeWords,
                errorCorrectionCodeWords, cols, rows);

        //2. step: construct data codewords
        final int n = getNumberOfDataCodewords(sourceCodeWords, errorCorrectionLevel,
                cols);
        if (n > 929) {
            throw new IllegalArgumentException(
                    "Encoded message contains to many code words, message to big ("
                            + msg.length() + " bytes)");
        }

        final StringBuilder sb = new StringBuilder(n);
        sb.append((char)n);
        sb.append(highLevel);
        for (int i = 0; i < pad; i++) {
            sb.append((char)900); //PAD characters
        }
        final String dataCodewords = sb.toString();

        //3. step: Error correction
        final String ec = PDF417ErrorCorrection.generateErrorCorrection(
                dataCodewords, errorCorrectionLevel);
        final String fullCodewords = dataCodewords + ec;

        //4. step: low-level encoding
        logic.startBarcode(msg, msg);
        encodeLowLevel(fullCodewords, cols, rows, errorCorrectionLevel, logic);
        logic.endBarcode();
    }

    /**
     * Determine optimal nr of columns and rows for the specified number of
     * codewords.
     * @param pdf417Bean contains configuration settings
     * @param sourceCodeWords number of code words
     * @return dimension object containing cols as width and rows as height
     */
    public static Dimension determineDimensions(PDF417Bean pdf417Bean,
            int sourceCodeWords) {

        final int minCols = pdf417Bean.getMinCols();
        final int maxCols = pdf417Bean.getMaxCols();
        final int maxRows = pdf417Bean.getMaxRows();
        final int minRows = pdf417Bean.getMinRows();
        final double preferredRatio = pdf417Bean.getWidthToHeightRatio();

        double ratio = 0;
        Dimension dimension = null;
        final int errorCorrectionCodeWords = PDF417ErrorCorrection.getErrorCorrectionCodewordCount(
                pdf417Bean.getErrorCorrectionLevel());

        for (int cols = minCols; cols <= maxCols; cols++) {

            final int rows = calculateNumberOfRows(sourceCodeWords,
                    errorCorrectionCodeWords, cols);

            if (rows < minRows) {
                break;
            }

            if (rows > maxRows) {
                continue;
            }

            final double newRatio = ((17 * cols + 69) * pdf417Bean.getModuleWidth())
                    / (rows * pdf417Bean.getBarHeight());

            // ignore if previous ratio is closer to preferred ratio
            if (dimension != null
                    && Math.abs(newRatio - preferredRatio) > Math.abs(ratio
                            - preferredRatio)) {
                continue;
            }

            ratio = newRatio;
            dimension = new Dimension(cols, rows);
        }

        return dimension;
    }
}
