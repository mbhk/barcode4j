/*
 * Copyright 2012 Jeremias Maerki, Switzerland
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

package org.krysalis.barcode4j.impl.qr;

import static org.krysalis.barcode4j.impl.qr.QRConstants.*;
import org.krysalis.barcode4j.tools.CheckUtil;

/**
 * High-level encoder for QR Code.
 *
 * @version 1.3
 */
public class QRHighLevelEncoder {

    private final int encodingMode;

    public QRHighLevelEncoder(String msg) {
        this.encodingMode = analyzeMessage(msg);
    }

    public int getEncodingMode() {
        return this.encodingMode;
    }

    private int analyzeMessage(String msg) {
        int mode = NUMERIC;
        for (int i = 0; i < msg.length(); i++) {
            final char ch = msg.charAt(i);
            if (!CheckUtil.isDigit(ch)) {
                if (mode == NUMERIC && isAlphanumeric(ch)) {
                    mode = ALPHANUMERIC;
                } else {
                    mode = BINARY;
                    break;
                }
            }
        }
        return mode;
    }
}
