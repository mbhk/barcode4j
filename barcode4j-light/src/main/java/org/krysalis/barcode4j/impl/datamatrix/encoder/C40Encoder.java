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

/**
 *
 * @author mk
 */
class C40Encoder implements Encoder {

    @Override
    public Encodation getEncodingMode() {
        return Encodation.C40_ENCODATION;
    }

    @Override
    public void encode(EncoderContext context) {
        //step C
        int lastCharSize = -1;
        final StringBuilder buffer = new StringBuilder();
        outerloop:
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
                if ((buffer.length() % 3) == 2) {
                    if (available < 2 || available > 2) {
                        lastCharSize = backtrackOneCharacter(context, buffer, removed,
                                lastCharSize);
                    }
                }
                while ((buffer.length() % 3) == 1
                        && ((lastCharSize <= 3 && available != 1) || lastCharSize > 3)) {
                    lastCharSize = backtrackOneCharacter(context, buffer, removed,
                            lastCharSize);
                }
                break outerloop;
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
            } else {
                //No unlatch
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
        if (c == ' ') {
            sb.append('\3');
            return 1;
        } else if (c >= '0' && c <= '9') {
            sb.append((char) (c - 48 + 4));
            return 1;
        } else if (c >= 'A' && c <= 'Z') {
            sb.append((char) (c - 65 + 14));
            return 1;
        } else if (c >= '\0' && c <= '\u001f') {
            sb.append('\0'); //Shift 1 Set
            sb.append(c);
            return 2;
        } else if (c >= '!' && c <= '/') {
            sb.append('\1'); //Shift 2 Set
            sb.append((char) (c - 33));
            return 2;
        } else if (c >= ':' && c <= '@') {
            sb.append('\1'); //Shift 2 Set
            sb.append((char) (c - 58 + 15));
            return 2;
        } else if (c >= '[' && c <= '_') {
            sb.append('\1'); //Shift 2 Set
            sb.append((char) (c - 91 + 22));
            return 2;
        } else if (c >= '\u0060' && c <= '\u007f') {
            sb.append('\2'); //Shift 3 Set
            sb.append((char) (c - 96));
            return 2;
        } else if (c >= '\u0080') {
            sb.append("\1\u001e"); //Shift 2, Upper Shift
            int len = 2;
            len += encodeChar((char) (c - 128), sb);
            return len;
        } else {
            throw new IllegalArgumentException("Illegal character: " + c);
        }
    }

    protected String encodeToCodewords(StringBuilder sb, int startPos) {
        final char c1 = sb.charAt(startPos);
        final char c2 = sb.charAt(startPos + 1);
        final char c3 = sb.charAt(startPos + 2);
        final int v = (1600 * c1) + (40 * c2) + c3 + 1;
        final char cw1 = (char) (v / 256);
        final char cw2 = (char) (v % 256);
        return "" + cw1 + cw2;
    }
}
