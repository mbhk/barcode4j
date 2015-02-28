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

import org.krysalis.barcode4j.tools.CheckUtil;

/**
 *
 * @author mk
 */
class TextEncoder extends C40Encoder {

    @Override
    public Encodation getEncodingMode() {
        return Encodation.TEXT_ENCODATION;
    }

    @Override
    protected int encodeChar(char c, StringBuilder sb) {
        final int res;
        if (CheckUtil.isSpace(c)) {
            res = append(sb, '\3');
        } else if (CheckUtil.isDigit(c)) {
            res = append(sb, (char) (c - '0' + 4));
        } else if (CheckUtil.isLowerAtoZ(c)) {
            res = append(sb, (char) (c - 'a' + 14));
        } else if (CheckUtil.intervallContains('\0', '\u001f', c)) {
            res = append(sb, SHIFT_1_SET, c);
        } else if (CheckUtil.intervallContains('!', '/', c)) {
            res = append(sb, SHIFT_2_SET, (char) (c - '!'));
        } else if (CheckUtil.intervallContains(':', '@', c)) {
            res = append(sb, SHIFT_2_SET, (char) (c - ':' + 15));
        } else if (CheckUtil.intervallContains('[', '_', c)) {
            res = append(sb, SHIFT_2_SET, (char) (c - '[' + 22));
        } else if (c == '\u0060') {
            res = append(sb, SHIFT_3_SET, (char) (c - '\u0060'));
        } else if (CheckUtil.isUpperAtoZ(c)) {
            res = append(sb, SHIFT_3_SET, (char) (c - 'A' + 1));
        } else if (CheckUtil.intervallContains('{', '\u007f', c)) {
            res = append(sb, SHIFT_3_SET, (char) (c - '{' + 27));
        } else if (c >= '\u0080') {
            final int len = append(sb, SHIFT_2_SET, SHIFT_UPPER);
            res = len + encodeChar((char) (c - 128), sb);
        } else {
            throw LookAhead.throwIllegalCharacter(c);
        }
        return res;
    }
}
