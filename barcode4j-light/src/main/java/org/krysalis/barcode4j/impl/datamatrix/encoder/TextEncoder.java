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
class TextEncoder extends C40Encoder {

    @Override
    public Encodation getEncodingMode() {
        return Encodation.TEXT_ENCODATION;
    }

    @Override
    protected int encodeChar(char c, StringBuilder sb) {
        if (c == ' ') {
            sb.append('\3');
            return 1;
        } else if (c >= '0' && c <= '9') {
            sb.append((char) (c - 48 + 4));
            return 1;
        } else if (c >= 'a' && c <= 'z') {
            sb.append((char) (c - 97 + 14));
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
        } else if (c == '\u0060') {
            sb.append('\2'); //Shift 3 Set
            sb.append((char) (c - 96));
            return 2;
        } else if (c >= 'A' && c <= 'Z') {
            sb.append('\2'); //Shift 3 Set
            sb.append((char) (c - 65 + 1));
            return 2;
        } else if (c >= '{' && c <= '\u007f') {
            sb.append('\2'); //Shift 3 Set
            sb.append((char) (c - 123 + 27));
            return 2;
        } else if (c >= '\u0080') {
            sb.append("\1\u001e"); //Shift 2, Upper Shift
            int len = 2;
            len += encodeChar((char) (c - 128), sb);
            return len;
        } else {
            LookAhead.throwIllegalCharacter(c);
            return -1;
        }
    }

}
