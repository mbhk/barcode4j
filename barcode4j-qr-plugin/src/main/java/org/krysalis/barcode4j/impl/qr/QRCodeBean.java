/*
 * Copyright 2012 Jeremias Maerki.
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

import java.awt.Dimension;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.DefaultTwoDimCanvasLogicHandler;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.tools.ECIUtil;
import org.krysalis.barcode4j.tools.UnitConv;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is an implementation of QR Code (ISO 18004:2006(E)).
 *
 * @version 1.2
 */
public class QRCodeBean extends AbstractBarcodeBean {

    /** The default module width (dot size) for DataMatrix. */
    protected static final double DEFAULT_MODULE_WIDTH = UnitConv.in2mm(1.0 / 72); //1px at 72dpi

    /** Error correction level */
    private char errorCorrectionLevel = QRConstants.ERROR_CORRECTION_LEVEL_L;
    /** Message encoding */
    private String encoding = "ISO-8859-1"; //ECI 000003
    /** Optional: the minimum size of the symbol. */
    private Dimension minSize;
    /** Optional: the maximum size of the symbol. */
    private Dimension maxSize;

    /** Create a new instance. */
    public QRCodeBean() {
        this.height = 0.0; //not used by DataMatrix
        this.moduleWidth = DEFAULT_MODULE_WIDTH;
        setQuietZone(QRConstants.QUIET_ZONE_SIZE * moduleWidth);
    }

    /**
     * Sets the error correction level.
     * @param level the error correction level (one of L, M, Q and H)
     */
    public void setErrorCorrectionLevel(char level) {
        switch (level) {
        case QRConstants.ERROR_CORRECTION_LEVEL_L:
        case QRConstants.ERROR_CORRECTION_LEVEL_M:
        case QRConstants.ERROR_CORRECTION_LEVEL_Q:
        case QRConstants.ERROR_CORRECTION_LEVEL_H:
            this.errorCorrectionLevel = level;
            break;
        default:
            throw new IllegalArgumentException(
                    "Invalid error correction level. Valid levels are: L, M, Q and H");
        }
    }

    /**
     * Returns the selected error correction level.
     * @return the error correction level (one of L, M, Q and H)
     */
    public char getErrorCorrectionLevel() {
        return this.errorCorrectionLevel;
    }

    /**
     * Sets the message encoding. The value must conform to one of Java's encodings and
     * have a mapping in the ECI registry.
     * @param encoding the message encoding
     */
    public void setEncoding(String encoding) {
        if (ECIUtil.getECIForEncoding(encoding) < 0) {
            throw new IllegalArgumentException("Not a valid encoding: " + encoding);
        }
        this.encoding = encoding;
    }

    /**
     * Returns the message encoding.
     * @return the message encoding (default is "ISO-8859-1")
     */
    public String getEncoding() {
        return this.encoding;
    }

    /**
     * Sets the minimum symbol size that is to be produced.
     * @param minSize the minimum size (in pixels), or null for no constraint
     */
    public void setMinSize(Dimension minSize) {
        this.minSize = minSize == null ? null : new Dimension(minSize);
    }

    /**
     * Returns the minimum symbol size that is to be produced. If the method returns null,
     * there's no constraint on the symbol size.
     * @return the minimum symbol size (in pixels), or null if there's no size constraint
     */
    public Dimension getMinSize() {
        return this.minSize == null ? null : new Dimension(this.minSize);
    }

    /**
     * Sets the maximum symbol size that is to be produced.
     * @param maxSize the maximum size (in pixels), or null for no constraint
     */
    public void setMaxSize(Dimension maxSize) {
        this.maxSize = maxSize == null ? null : new Dimension(maxSize);
    }

    /**
     * Returns the maximum symbol size that is to be produced. If the method returns null,
     * there's no constraint on the symbol size.
     * @return the maximum symbol size (in pixels), or null if there's no size constraint
     */
    public Dimension getMaxSize() {
        return this.maxSize == null ? null : new Dimension(this.maxSize);
    }

    @Override
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if (msg == null || msg.isEmpty()) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        final TwoDimBarcodeLogicHandler handler =
                new DefaultTwoDimCanvasLogicHandler(this, new Canvas(canvas));

        final QRLogicImpl impl = new QRLogicImpl();
        impl.generateBarcodeLogic(handler, msg, encoding, errorCorrectionLevel,
                getMinSize(), getMaxSize());
    }

    @Override
    public BarcodeDimension calcDimensions(String msg) {
        QRCode code = null;
        try {
            code = Encoder.encode(msg,
                    QRLogicImpl.getZXingErrorLevel(errorCorrectionLevel),
                    QRLogicImpl.createHints(encoding));
        } catch (WriterException e) {
            throw new IllegalStateException(e);
        }
        final ByteMatrix matrix = code.getMatrix();
        final int effWidth = matrix.getWidth();
        final int effHeight = matrix.getHeight();
        checkSizeConstraints(effWidth, effHeight);

        final double width = effWidth * getModuleWidth();
        final double height = effHeight * getBarHeight();
        final double qzh = hasQuietZone() ? getQuietZone() : 0;
        final double qzv = hasQuietZone() ? getVerticalQuietZone() : 0;
        return new BarcodeDimension(width, height,
                width + (2 * qzh), height + (2 * qzv),
                qzh, qzv);
    }

    private void checkSizeConstraints(int width, int height) {
        //Note: we're only checking the constraints, we can't currently influence ZXing's encoder.
        if (this.minSize != null && (width < this.minSize.width || height < this.minSize.height)) {
            throw new IllegalArgumentException(
                    "The given message would result in a smaller symbol than required."
                    + " Requested minimum: "
                    + this.minSize.width + " x " + this.minSize.height
                    + ", effective: "
                    + width + " x " + height);
        }
        if (this.maxSize != null && (width > this.maxSize.width || height > this.maxSize.height)) {
            throw new IllegalArgumentException(
                    "The given message would result in a larger symbol than required."
                    + " Requested maximum: "
                    + this.maxSize.width + " x " + this.maxSize.height
                    + ", effective: "
                    + width + " x " + height);
        }
    }

    @Override
    public double getVerticalQuietZone() {
        return getQuietZone();
    }

    @Override
    public double getBarWidth(int width) {
        return moduleWidth;
    }

    @Override
    public double getBarHeight() {
        return moduleWidth;
    }

    @Override
    public String getId() {
        return "qr";
    }

    @Override
    public Collection<String> getAdditionalNames() {
        final Set<String> res = new TreeSet<String>();
        res.add("qrcode");
        res.add("qr-code");
        return res;
    }
}
