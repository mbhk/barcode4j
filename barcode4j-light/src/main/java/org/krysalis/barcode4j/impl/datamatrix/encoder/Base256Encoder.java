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
class Base256Encoder implements Encoder {

    @Override
    public Encodation getEncodingMode() {
        return Encodation.BASE256_ENCODATION;
    }

    @Override
    public void encode(EncoderContext context) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append('\0'); //Initialize length field
        while (context.hasMoreCharacters()) {
            final char c = context.getCurrentChar();
            buffer.append(c);

            context.incPos();

            final Encodation newMode = LookAhead.lookAheadTest(context.getMessage(), context.getPos(), getEncodingMode());
            if (newMode != getEncodingMode()) {
                context.signalEncoderChange(newMode);
                break;
            }
        }
        final int dataCount = buffer.length() - 1;
        final int lengthFieldSize = 1;
        final int currentSize = context.getCodewordCount() + dataCount + lengthFieldSize;
        context.updateSymbolInfo(currentSize);
        final boolean mustPad = (context.getSymbolInfo().getDataCapacity() - currentSize) > 0;
        if (context.hasMoreCharacters() || mustPad) {
            if (dataCount <= 249) {
                buffer.setCharAt(0, (char) dataCount);
            } else if (dataCount > 249 && dataCount <= 1555) {
                buffer.setCharAt(0, (char) ((dataCount / 250) + 249));
                buffer.insert(1, (char) (dataCount % 250));
            } else {
                throw new IllegalStateException(
                        "Message length not in valid ranges: " + dataCount);
            }
        }
        for (int i = 0; i < buffer.length(); i++) {
            context.writeCodeword(randomize255State(
                    buffer.charAt(i), context.getCodewordCount() + 1));
        }
    }
private static char randomize255State(char ch, int codewordPosition) {
        final int pseudoRandom = ((149 * codewordPosition) % 255) + 1;
        final int tempVariable = ch + pseudoRandom;
        if (tempVariable <= 255) {
            return (char)tempVariable;
        } else {
            return (char)(tempVariable - 256);
        }
    }
}
