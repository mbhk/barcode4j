/*
 * Copyright 2006-2007 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.datamatrix;

import java.awt.Dimension;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.krysalis.barcode4j.tools.URLUtil;
import static org.krysalis.barcode4j.impl.datamatrix.DataMatrixConstants.*;
import org.krysalis.barcode4j.impl.datamatrix.encoder.Encodation;
import org.krysalis.barcode4j.impl.datamatrix.encoder.EncoderContext;

/**
 * DataMatrix ECC 200 data encoder following the algorithm described in ISO/IEC
 * 16022:200(E) in annex S.
 *
 * @version 2.0
 */
public class DataMatrixHighLevelEncoder {

    private static final String DEFAULT_ASCII_ENCODING = "ISO-8859-1";
    private static final Logger LOGGER = Logger.getLogger(DataMatrixHighLevelEncoder.class.getName());

    private DataMatrixHighLevelEncoder() {
        // hide default public constructor
    }

    /**
     * Performs message encoding of a DataMatrix message using the algorithm
     * described in annex P of ISO/IEC 16022:2000(E).
     *
     * @param msg the message
     * @return the encoded message (the char values range from 0 to 255)
     * @throws IOException if an I/O error occurs while fetching external data
     */
    public static String encodeHighLevel(String msg) throws IOException {
        return encodeHighLevel(msg, SymbolShapeHint.FORCE_NONE, null, null);
    }

    /**
     * Performs message encoding of a DataMatrix message using the algorithm
     * described in annex P of ISO/IEC 16022:2000(E).
     *
     * @param msg the message
     * @param shape requested shape. May be
     * <code>SymbolShapeHint.FORCE_NONE</code>,
     * <code>SymbolShapeHint.FORCE_SQUARE</code> or
     * <code>SymbolShapeHint.FORCE_RECTANGLE</code>.
     * @param minSize the minimum symbol size constraint or null for no
     * constraint
     * @param maxSize the maximum symbol size constraint or null for no
     * constraint
     * @return the encoded message (the char values range from 0 to 255)
     * @throws IOException if an I/O error occurs while fetching external data
     */
    public static String encodeHighLevel(String msg,
            SymbolShapeHint shape, Dimension minSize, Dimension maxSize) throws IOException {
        //the codewords 0..255 are encoded as Unicode characters
        Encodation encodingMode = Encodation.ASCII_ENCODATION; //Default mode
        final EncoderContext context = createEncoderContext(msg);
        context.setSymbolShape(shape);
        context.setSizeConstraints(minSize, maxSize);

        if (msg.startsWith(MACRO_05_HEADER) && msg.endsWith(MACRO_TRAILER)) {
            context.writeCodeword(MACRO_05);
            context.setSkipAtEnd(2);
            context.incPos(MACRO_05_HEADER.length());
        } else if (msg.startsWith(MACRO_06_HEADER) && msg.endsWith(MACRO_TRAILER)) {
            context.writeCodeword(MACRO_06);
            context.setSkipAtEnd(2);
            context.incPos(MACRO_06_HEADER.length());
        }

        while (context.hasMoreCharacters()) {
            context.encode(encodingMode);
            if (context.getNewEncoding() != null) {
                encodingMode = context.getNewEncoding();
                context.resetEncoderSignal();
            }
        }
        final int len = context.getCodewordCount();
        context.updateSymbolInfo();
        final int capacity = context.getSymbolInfo().getDataCapacity();
        if (len < capacity) {
            if (encodingMode != Encodation.ASCII_ENCODATION && encodingMode != Encodation.BASE256_ENCODATION) {
                LOGGER.log(Level.FINE, "Unlatch because symbol isn't filled up");
                context.writeCodeword('\u00fe'); //Unlatch (254)
            }
        }
        //Padding
        if (context.getCodewordCount() < capacity) {
            context.writeCodeword(DataMatrixConstants.PAD);
        }
        while (context.getCodewordCount() < capacity) {
            context.writeCodeword(randomize253State(DataMatrixConstants.PAD, context.getCodewordCount() + 1));
        }

        return context.getCodeword();
    }

    private static char randomize253State(char ch, int codewordPosition) {
        final int pseudoRandom = ((149 * codewordPosition) % 253) + 1;
        final int tempVariable = ch + pseudoRandom;
        if (tempVariable <= 254) {
            return (char) tempVariable;
        } else {
            return (char) (tempVariable - 254);
        }
    }

    private static EncoderContext createEncoderContext(String msg) throws IOException {
        final String url = URLUtil.getURL(msg);
        if (url != null) {
            //URL processing
            final byte[] data = URLUtil.getData(url, DEFAULT_ASCII_ENCODING);
            return new EncoderContext(data);
        } else {
            return new EncoderContext(msg);
        }
    }
}
