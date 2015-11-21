package org.krysalis.barcode4j.impl.qr;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeGeneratorProvider;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

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
        assertEquals(message, res.getText());
        assertEquals(BarcodeFormat.QR_CODE, res.getBarcodeFormat());
    }
    
    // TODO zusammenführen mit OutputFormatTest aus barcode4j-light
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
