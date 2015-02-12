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
package org.krysalis.barcode4j.integration;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeGeneratorProvider;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

/**
 *
 * @author mk
 */
public class OutputFormatTest {

    private static final Logger LOGGER = Logger.getLogger(OutputFormatTest.class.getName());
    BarcodeGeneratorProvider provider;
    File outFile;
    OutputStream out;

    @Before
    public void setup() throws BarcodeException, IOException {
        provider = BarcodeGeneratorProvider.getInstance();
        outFile = File.createTempFile("b4jTest", null);
        LOGGER.log(Level.INFO, "TmpFile: {0}", outFile.getAbsolutePath());
        out = new java.io.FileOutputStream(outFile);
    }

    @After
    public void tearDown() {
        outFile.delete();
    }

    @Test
    public void createAndDecodeEan13Barcode() throws BarcodeException, IOException, NotFoundException {
        String message = "9783935551007";
        BarcodeGenerator gen = provider.getBarcodeGenerator("ean-13");
        BitmapCanvasProvider bitmap = new BitmapCanvasProvider(out,
                "image/x-png", 300, BufferedImage.TYPE_BYTE_GRAY, true, Orientation.ZERO);
        gen.generateBarcode(bitmap, message);
        bitmap.finish();
        try {
            out.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        Result res = decode(outFile.getAbsolutePath());
        TestCase.assertEquals(message, res.getText());
        TestCase.assertEquals(BarcodeFormat.EAN_13, res.getBarcodeFormat());
    }

    @Test
    public void createAndDecodeDatamatrixCode() throws BarcodeException, IOException, NotFoundException {
        String message = "visit http://barcode4j.sourceforge.net";
        BarcodeGenerator gen = provider.getBarcodeGenerator("datamatrix");
        BitmapCanvasProvider bitmap = new BitmapCanvasProvider(out,
                "image/x-png", 300, BufferedImage.TYPE_BYTE_GRAY, true, Orientation.NINETY);
        gen.generateBarcode(bitmap, message);
        bitmap.finish();
        try {
            out.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        Result res = decode(outFile.getAbsolutePath());
        TestCase.assertEquals(message, res.getText());
        TestCase.assertEquals(BarcodeFormat.DATA_MATRIX, res.getBarcodeFormat());
    }

    @Test
    public void createAndDecodeQRCode() throws BarcodeException, IOException, NotFoundException {
        String message = "visit http://barcode4j.sourceforge.net";
        BarcodeGenerator gen = provider.getBarcodeGenerator("qr-code");
        BitmapCanvasProvider bitmap = new BitmapCanvasProvider(out,
                "image/x-png", 300, BufferedImage.TYPE_BYTE_BINARY, false, Orientation.ONEHUNDRED_EIGHTY);
        gen.generateBarcode(bitmap, message);
        bitmap.finish();
        try {
            out.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        Result res = decode(outFile.getAbsolutePath());
        TestCase.assertEquals(message, res.getText());
        TestCase.assertEquals(BarcodeFormat.QR_CODE, res.getBarcodeFormat());
    }

    private Result decode(String filename) throws IOException, NotFoundException {
        InputStream barCodeInputStream = new FileInputStream(filename);
        BufferedImage barCodeBufferedImage = ImageIO.read(barCodeInputStream);

        LuminanceSource source = new BufferedImageLuminanceSource(barCodeBufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new MultiFormatReader();
        Result result = null;
        try {
            result = reader.decode(bitmap);
            System.out.println("Barcode text is " + result.getText());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failure during decoding", e);
        }

        return result;
    }
}
