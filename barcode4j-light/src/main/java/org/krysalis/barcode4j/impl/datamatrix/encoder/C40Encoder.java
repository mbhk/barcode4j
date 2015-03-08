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

import static org.krysalis.barcode4j.impl.datamatrix.DataMatrixConstants.C40_UNLATCH;
import org.krysalis.barcode4j.tools.CheckUtil;

/**
 *
 * @author mk
 */
class C40Encoder implements Encoder {

    protected static final char SHIFT_UPPER = '\u001e';
    protected static final char SHIFT_3_SET = '\2';
    protected static final char SHIFT_2_SET = '\1';
    protected static final char SHIFT_1_SET = '\0';

    @Override
    public Encodation getEncodingMode() {
        return Encodation.C40_ENCODATION;
    }

    @Override
    public void encode(EncoderContext context) {
        //step C
        int lastCharSize = -1;
        final StringBuilder buffer = new StringBuilder();

        while (context.hasMoreCharacters()) {
            final char c = context.getCurrentChar();
            context.incPos();

            lastCharSize = encodeChar(c, buffer);

            final int unwritten = (buffer.length() / 3) * 2;

            final int curCodewordCount = context.getCodewordCount() + unwritten;
            context.updateSymbolInfo(curCodewordCount);
            final int available = context.getSymbolInfo().getDataCapacity() - curCodewordCount;

            if (!context.hasMoreCharacters()) {
                //Avoid having a single C40 value in the last triplet
                final StringBuilder removed = new StringBuilder();
                if ((buffer.length() % 3) == 2 && (available < 2 || available > 2)) {
                    lastCharSize = backtrackOneCharacter(context, buffer, removed,
                            lastCharSize);
                }
                while ((buffer.length() % 3) == 1
                        && ((lastCharSize <= 3 && available != 1) || lastCharSize > 3)) {
                    lastCharSize = backtrackOneCharacter(context, buffer, removed,
                            lastCharSize);
                }
                break;
            }

            final int count = buffer.length();
            if ((count % 3) == 0) {
                final Encodation newMode = LookAhead.lookAheadTest(context.getMessage(), context.getPos(), getEncodingMode());
                if (newMode != getEncodingMode()) {
                    context.signalEncoderChange(newMode);
                    break;
                }
            }
        }
        handleEOD(context, buffer, lastCharSize);
    }

    private int backtrackOneCharacter(EncoderContext context,
            StringBuilder buffer, StringBuilder removed, int lastCharSize) {
        final int count = buffer.length();
        buffer.delete(count - lastCharSize, count);
        context.incPos(-1);
        final char c = context.getCurrentChar();
        lastCharSize = encodeChar(c, removed);
        context.resetSymbolInfo(); //Deal with possible reduction in symbol size
        return lastCharSize;
    }

    protected void writeNextTriplet(EncoderContext context, StringBuilder buffer) {
        context.writeCodewords(encodeToCodewords(buffer, 0));
        buffer.delete(0, 3);
    }

    /**
     * Handle "end of data" situations
     *
     * @param context the encoder context
     * @param buffer the buffer with the remaining encoded characters
     */
    protected void handleEOD(EncoderContext context, StringBuilder buffer, int lastCharSize) {
        final int unwritten = (buffer.length() / 3) * 2;
        final int rest = buffer.length() % 3;

        final int curCodewordCount = context.getCodewordCount() + unwritten;
        context.updateSymbolInfo(curCodewordCount);
        final int available = context.getSymbolInfo().getDataCapacity() - curCodewordCount;

        if (rest == 2) {
            buffer.append('\0'); //Shift 1
            while (buffer.length() >= 3) {
                writeNextTriplet(context, buffer);
            }
            if (context.hasMoreCharacters()) {
                context.writeCodeword(C40_UNLATCH);
            }
        } else if (available == 1 && rest == 1) {
            while (buffer.length() >= 3) {
                writeNextTriplet(context, buffer);
            }
            if (context.hasMoreCharacters()) {
                context.writeCodeword(C40_UNLATCH);
            }
            context.incPos(-1);
        } else if (rest == 0) {
            while (buffer.length() >= 3) {
                writeNextTriplet(context, buffer);
            }
            if (available > 0 || context.hasMoreCharacters()) {
                context.writeCodeword(C40_UNLATCH);
            }
        } else {
            throw new IllegalStateException("Unexpected case. Please report!");
        }
        context.signalEncoderChange(Encodation.ASCII_ENCODATION);
    }

    protected int encodeChar(char c, StringBuilder sb) {
        final int res;
        if (CheckUtil.isSpace(c)) {
            res = append(sb, '\3');
        } else if (CheckUtil.isDigit(c)) {
            res = append(sb, (char) (c - '0' + 4));
        } else if (CheckUtil.isUpperAtoZ(c)) {
            res = append(sb, (char) (c - 'A' + 14));
        } else if (CheckUtil.intervallContains('\0', '\u001f', c)) {
            res = append(sb, SHIFT_1_SET, c);
        } else if (CheckUtil.intervallContains('!', '/', c)) {
            res = append(sb,SHIFT_2_SET,(char) (c - '!'));
        } else if (CheckUtil.intervallContains(':', '@', c)) {
            res = append(sb, SHIFT_2_SET, (char) (c - ':' + 15));
        } else if (CheckUtil.intervallContains('[', '_', c)) {
            res = append(sb, SHIFT_2_SET, (char) (c - '[' + 22));
        } else if (CheckUtil.intervallContains('\u0060', '\u007f', c)) {
            res = append(sb, SHIFT_3_SET, (char) (c - '\u0060'));
        } else if (c >= '\u0080') {
            final int len = append(sb, SHIFT_2_SET, SHIFT_UPPER);
            res = len + encodeChar((char) (c - 128), sb);
        } else {
            throw LookAhead.throwIllegalCharacter(c);
        }
        return res;
    }

    protected String encodeToCodewords(StringBuilder sb, int startPos) {
        final char c1 = sb.charAt(startPos);
        final char c2 = sb.charAt(startPos + 1);
        final char c3 = sb.charAt(startPos + 2);
        final int v = (1600 * c1) + (40 * c2) + c3 + 1;
        final char cw1 = (char) (v / 256);
        final char cw2 = (char) (v % 256);
        return String.valueOf(cw1) + cw2;
    }

    protected int append(StringBuilder sb, char... chars) {
        for (final char c : chars) {
            sb.append(c);
        }
        return chars.length;
    }
}
