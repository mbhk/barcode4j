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

import static org.krysalis.barcode4j.impl.datamatrix.DataMatrixConstants.*;
import org.krysalis.barcode4j.tools.CheckUtil;

/**
 *
 * @author mk
 */
class AsciiEncoder implements Encoder {

    @Override
    public Encodation getEncodingMode() {
        return Encodation.ASCII_ENCODATION;
    }

    @Override
    public void encode(EncoderContext context) {
        //step B
        final int n = determineConsecutiveDigitCount(context.getMessage(), context.getPos());
        if (n >= 2) {
            context.writeCodeword(encodeASCIIDigits(context.getMessage().charAt(context.getPos()),
                    context.getMessage().charAt(context.getPos() + 1)));
            context.incPos(2);
        } else {
            final char c = context.getCurrentChar();
            final Encodation newMode = LookAhead.lookAheadTest(context.getMessage(), context.getPos(), getEncodingMode());
            if (newMode != getEncodingMode()) {
                switch (newMode) {
                    case BASE256_ENCODATION:
                        context.writeCodeword(LATCH_TO_BASE256);
                        context.signalEncoderChange(Encodation.BASE256_ENCODATION);
                        return;
                    case C40_ENCODATION:
                        context.writeCodeword(LATCH_TO_C40);
                        context.signalEncoderChange(Encodation.C40_ENCODATION);
                        return;
                    case X12_ENCODATION:
                        context.writeCodeword(LATCH_TO_ANSIX12);
                        context.signalEncoderChange(Encodation.X12_ENCODATION);
                        break;
                    case TEXT_ENCODATION:
                        context.writeCodeword(LATCH_TO_TEXT);
                        context.signalEncoderChange(Encodation.TEXT_ENCODATION);
                        break;
                    case EDIFACT_ENCODATION:
                        context.writeCodeword(LATCH_TO_EDIFACT);
                        context.signalEncoderChange(Encodation.EDIFACT_ENCODATION);
                        break;
                    default:
                        throw new IllegalStateException("Illegal mode: " + newMode);
                }
            } else if (CheckUtil.isExtendedASCII(c)) {
                context.writeCodeword(UPPER_SHIFT);
                context.writeCodeword((char) (c - 128 + 1));
                context.incPos();
            } else {
                context.writeCodeword((char) (c + 1));
                context.incPos();
            }

        }
    }

    private static char encodeASCIIDigits(char digit1, char digit2) {
        if (CheckUtil.isDigit(digit1) && CheckUtil.isDigit(digit2)) {
            final int num = (digit1 - '0') * 10 + (digit2 - '0');
            return (char) (num + 130);
        } else {
            throw new IllegalArgumentException("not digits: " + digit1 + digit2);
        }
    }

    /**
     * Determines the number of consecutive characters that are encodable using
     * numeric compaction.
     *
     * @param msg the message
     * @param startpos the start position within the message
     * @return the requested character count
     */
    private static int determineConsecutiveDigitCount(String msg, int startpos) {
        int count = 0;
        final int len = msg.length();
        int idx = startpos;
        if (idx < len) {
            char ch = msg.charAt(idx);
            while (CheckUtil.isDigit(ch) && idx < len) {
                count++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
        }
        return count;
    }
}
