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

import static org.krysalis.barcode4j.impl.datamatrix.DataMatrixConstants.X12_UNLATCH;

/**
 *
 * @author mk
 */
class X12Encoder extends C40Encoder {

    @Override
    public Encodation getEncodingMode() {
        return Encodation.X12_ENCODATION;
    }

    @Override
    public void encode(EncoderContext context) {
        //step C
        final StringBuilder buffer = new StringBuilder();
        while (context.hasMoreCharacters()) {
            final char c = context.getCurrentChar();
            context.incPos();

            encodeChar(c, buffer);

            final int count = buffer.length();
            if ((count % 3) == 0) {
                writeNextTriplet(context, buffer);

                final Encodation newMode = LookAhead.lookAheadTest(context.getMessage(), context.getPos(), getEncodingMode());
                if (newMode != getEncodingMode()) {
                    context.signalEncoderChange(newMode);
                    break;
                }
            }
        }
        handleEOD(context, buffer);
    }

    @Override
    protected int encodeChar(char c, StringBuilder sb) {
        if (c == '\r') {
            sb.append('\0');
        } else if (c == '*') {
            sb.append('\1');
        } else if (c == '>') {
            sb.append('\2');
        } else if (c == ' ') {
            sb.append('\3');
        } else if (c >= '0' && c <= '9') {
            sb.append((char) (c - 48 + 4));
        } else if (c >= 'A' && c <= 'Z') {
            sb.append((char) (c - 65 + 14));
        } else {
            LookAhead.throwIllegalCharacter(c);
        }
        return 1;
    }

    protected void handleEOD(EncoderContext context, StringBuilder buffer) {
        context.updateSymbolInfo();
        final int available = context.getSymbolInfo().getDataCapacity() - context.getCodewordCount();
        final int count = buffer.length();
        if (count == 2) {
            context.writeCodeword(X12_UNLATCH);
            context.incPos(-2);
            context.signalEncoderChange(Encodation.ASCII_ENCODATION);
        } else if (count == 1) {
            context.incPos(-1);
            if (available > 1) {
                context.writeCodeword(X12_UNLATCH);
            } else {
                //NOP - No unlatch necessary
            }
            context.signalEncoderChange(Encodation.ASCII_ENCODATION);
        }
    }
}
