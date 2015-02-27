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

import java.awt.Dimension;
import java.io.UnsupportedEncodingException;
import java.util.EnumMap;
import java.util.Map;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixSymbolInfo;
import org.krysalis.barcode4j.impl.datamatrix.SymbolShapeHint;

/**
 *
 * @author mk
 */
public class EncoderContext {

    private static final String DEFAULT_ASCII_ENCODING = "ISO-8859-1";

    private final String msg;
    private SymbolShapeHint shape = SymbolShapeHint.FORCE_NONE;
    private Dimension minSize;
    private Dimension maxSize;
    private final StringBuilder codewords;
    private int pos = 0;
    private Encodation newEncoding = null;
    private DataMatrixSymbolInfo symbolInfo;
    private int skipAtEnd = 0;
    private final Map<Encodation, Encoder> encoders = new EnumMap<Encodation, Encoder>(Encodation.class);

    public EncoderContext(String msg) {
        this(convertMessageToBinary(msg));
    }

    public EncoderContext(byte[] data) {
        //From this point on Strings are not Unicode anymore!
        final StringBuilder sb = new StringBuilder(data.length);
        for (int i = 0; i < data.length; i++) {
            final char ch = (char) (data[i] & 0xff);
            sb.append(ch);
        }
        this.msg = sb.toString(); //Not Unicode here!
        this.codewords = new StringBuilder(msg.length());
        initializeEncoders();
    }

    private static byte[] convertMessageToBinary(String msg) {
        //From this point on Strings are not Unicode anymore!
        final byte[] msgBinary;
        try {
            msgBinary = msg.getBytes(DEFAULT_ASCII_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Unsupported encoding: " + e.getMessage());
        }
        final StringBuilder sb = new StringBuilder(msgBinary.length);
        for (int i = 0; i < msgBinary.length; i++) {
            final char ch = (char) (msgBinary[i] & 0xff);
            if (ch == '?' && msg.charAt(i) != '?') {
                throw new IllegalArgumentException("Message contains characters outside "
                        + DEFAULT_ASCII_ENCODING + " encoding.");
            }
            sb.append(ch);
        }
        return msgBinary;
    }

    public DataMatrixSymbolInfo getSymbolInfo() {
        return symbolInfo;
    }

    public void setSymbolShape(SymbolShapeHint shape) {
        this.shape = shape;
    }

    private void initializeEncoders() {
        encoders.put(Encodation.ASCII_ENCODATION, new AsciiEncoder());
        encoders.put(Encodation.BASE256_ENCODATION, new Base256Encoder());
        encoders.put(Encodation.C40_ENCODATION, new C40Encoder());
        encoders.put(Encodation.EDIFACT_ENCODATION, new EdifactEncoder());
        encoders.put(Encodation.TEXT_ENCODATION, new TextEncoder());
        encoders.put(Encodation.X12_ENCODATION, new X12Encoder());
    }

    public void setSizeConstraints(Dimension minSize, Dimension maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    public String getMessage() {
        return this.msg;
    }

    public void setSkipAtEnd(int count) {
        this.skipAtEnd = count;
    }

    public char getCurrentChar() {
        return msg.charAt(pos);
    }

    public char getCurrent() {
        return msg.charAt(pos);
    }

    public void writeCodewords(String codewords) {
        this.codewords.append(codewords);
    }

    public void writeCodeword(char codeword) {
        this.codewords.append(codeword);
    }

    public int getCodewordCount() {
        return this.codewords.length();
    }

    public void signalEncoderChange(Encodation encoding) {
        this.newEncoding = encoding;
    }

    public void resetEncoderSignal() {
        this.newEncoding = null;
    }

    public boolean hasMoreCharacters() {
        return pos < getTotalMessageCharCount();
    }

    private int getTotalMessageCharCount() {
        return msg.length() - skipAtEnd;
    }

    public int getRemainingCharacters() {
        return getTotalMessageCharCount() - pos;
    }

    public void updateSymbolInfo() {
        updateSymbolInfo(getCodewordCount());
    }

    public void updateSymbolInfo(int len) {
        if (this.symbolInfo == null || len > this.symbolInfo.dataCapacity) {
            this.symbolInfo = DataMatrixSymbolInfo.lookup(len, shape, minSize, maxSize, true);
        }
    }

    public void resetSymbolInfo() {
        this.symbolInfo = null;
    }

    public void incPos(int cnt) {
        pos += cnt;
    }

    public void incPos() {
        ++pos;
    }

    public int getPos() {
        return pos;
    }

    public String getCodeword() {
        return codewords.toString();
    }

    public void encode(Encodation enc) {
        if (enc == null) {
            return;
        }
        encoders.get(enc).encode(this);
    }

    public Encodation getNewEncoding() {
        return newEncoding;
    }
}
