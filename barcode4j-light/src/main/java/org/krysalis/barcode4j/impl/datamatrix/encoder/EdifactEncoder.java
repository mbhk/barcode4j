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

/**
 *
 * @author mk
 */
public class EdifactEncoder implements Encoder {

    @Override
    public Encodation getEncodingMode() {
        return Encodation.EDIFACT_ENCODATION;
    }

    @Override
    public void encode(EncoderContext context) {
        //step F
        final StringBuilder buffer = new StringBuilder();
        while (context.hasMoreCharacters()) {
            final char c = context.getCurrentChar();
            encodeChar(c, buffer);
            context.incPos();

            final int count = buffer.length();
            if (count >= 4) {
                context.writeCodewords(encodeToCodewords(buffer, 0));
                buffer.delete(0, 4);

                final Encodation newMode = LookAhead.lookAheadTest(context.getMessage(), context.getPos(), getEncodingMode());
                if (newMode != getEncodingMode()) {
                    context.signalEncoderChange(Encodation.ASCII_ENCODATION);
                    break;
                }
            }
        }
        buffer.append((char) 31); //Unlatch
        handleEOD(context, buffer);
    }

    /**
     * Handle "end of data" situations
     *
     * @param context the encoder context
     * @param buffer the buffer with the remaining encoded characters
     */
    protected void handleEOD(EncoderContext context, StringBuilder buffer) {
        try {
            final int count = buffer.length();
            if (count == 0) {
                return; //Already finished
            } else if (count == 1) {
                //Only an unlatch at the end
                context.updateSymbolInfo();
                final int available = context.getSymbolInfo().dataCapacity - context.getCodewordCount();
                final int remaining = context.getRemainingCharacters();
                if (remaining == 0 && available <= 2) {
                    return; //No unlatch
                }
            }

            if (count > 4) {
                throw new IllegalStateException("Count must not exceed 4");
            }
            final int restChars = count - 1;
            final String encoded = encodeToCodewords(buffer, 0);
            final boolean endOfSymbolReached = !context.hasMoreCharacters();
            boolean restInAscii = endOfSymbolReached && restChars <= 2;

            int available;
            if (restChars <= 2) {
                context.updateSymbolInfo(context.getCodewordCount() + restChars);
                available = context.getSymbolInfo().dataCapacity - context.getCodewordCount();
                if (available >= 3) {
                    restInAscii = false;
                    context.updateSymbolInfo(context.getCodewordCount() + encoded.length());
                        // TODO why was this code introduced?
                    //available = context.symbolInfo.dataCapacity - context.getCodewordCount();
                }
            }

            if (restInAscii) {
                context.resetSymbolInfo();
                context.incPos(-restChars);
            } else {
                context.writeCodewords(encoded);
            }
        } finally {
            context.signalEncoderChange(Encodation.ASCII_ENCODATION);
        }
    }

    protected void encodeChar(char c, StringBuilder sb) {
        if (c >= ' ' && c <= '?') {
            sb.append(c);
        } else if (c >= '@' && c <= '^') {
            sb.append((char) (c - 64));
        } else {
            LookAhead.throwIllegalCharacter(c);
        }
    }

    protected String encodeToCodewords(StringBuilder sb, int startPos) {
        final int len = sb.length() - startPos;
        if (len == 0) {
            throw new IllegalStateException("StringBuilder must not be empty");
        }
        final char c1 = sb.charAt(startPos);
        final char c2 = len >= 2 ? sb.charAt(startPos + 1) : 0;
        final char c3 = len >= 3 ? sb.charAt(startPos + 2) : 0;
        final char c4 = len >= 4 ? sb.charAt(startPos + 3) : 0;

        final int v = (c1 << 18) + (c2 << 12) + (c3 << 6) + c4;
        final char cw1 = (char) ((v >> 16) & 255);
        final char cw2 = (char) ((v >> 8) & 255);
        final char cw3 = (char) (v & 255);
        final StringBuilder res = new StringBuilder(3);
        res.append(cw1);
        if (len >= 2) {
            res.append(cw2);
        }
        if (len >= 3) {
            res.append(cw3);
        }
        return res.toString();
    }
}
