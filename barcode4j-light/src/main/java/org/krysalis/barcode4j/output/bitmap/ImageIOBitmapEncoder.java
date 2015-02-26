/*
 * Copyright 2002-2004,2010 Jeremias Maerki.
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
package org.krysalis.barcode4j.output.bitmap;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.krysalis.barcode4j.tools.MimeTypes;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * BitmapEncoder implementation using ImageIO.
 *
 * @author Jeremias Maerki
 * @author mk
 * @version 1.2
 */
public class ImageIOBitmapEncoder implements BitmapEncoder {

    private static final Logger LOGGER = Logger.getLogger(ImageIOBitmapEncoder.class.getName());

    /**
     * Constructs the BitmapEncoder. The constructor checks if the ImageIO API
     * is available so it doesn't get registered in case it's not there.
     *
     * @throws ClassNotFoundException if the ImageIO API is unavailable
     */
    public ImageIOBitmapEncoder() throws ClassNotFoundException {
        try {
            Class.forName("javax.imageio.ImageIO");
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.INFO, "{0} not available. This JVM does not support ImageIO.", ImageIOBitmapEncoder.class.getName());
            throw ex;
        }
    }

    @Override
    public String[] getSupportedMIMETypes() {
        return ImageIO.getWriterMIMETypes();
    }

    @Override
    public void encode(BufferedImage image, OutputStream out,
            String mime, int resolution) throws IOException {

        //Simply get first offered writer
        final Iterator i = ImageIO.getImageWritersByMIMEType(mime);
        final ImageWriter writer = (ImageWriter) i.next();

        //Prepare output
        final ImageOutputStream imout = ImageIO.createImageOutputStream(out);
        writer.setOutput(imout);

        //Prepare metadata
        final IIOMetadata iiometa = setupMetadata(image, writer, mime, resolution);

        //Write image
        final IIOImage iioimage = new IIOImage(image, null, iiometa);
        writer.write(iioimage);
        writer.dispose();
        imout.close();
    }

    private IIOMetadata setupMetadata(BufferedImage image, ImageWriter writer,
            String mime, int resolution) throws IOException {
        IIOMetadata iiometa;
        try {
            iiometa = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image),
                    writer.getDefaultWriteParam());
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "ImageIO has problems with metadata", e);
            return null;
        }
        if (iiometa == null) {
            return null; //Some JAI-codecs don't support metadata
        }

        final String stdmeta = "javax_imageio_1.0";
        final String jpegmeta = "javax_imageio_jpeg_image_1.0";

        if (!iiometa.isReadOnly()) {
            if (MimeTypes.MIME_JPEG.equals(mime)
                    && jpegmeta.equals(iiometa.getNativeMetadataFormatName())) {

                /* JPEG gets special treatment because I believe there's a bug in
                 * the JPEG codec in ImageIO converting the pixel size incorrectly
                 * when using standard metadata format. JM, 2003-10-28
                 */
                checkWritable(iiometa);

                final IIOMetadataNode rootnode = (IIOMetadataNode) iiometa.getAsTree(jpegmeta);
                final IIOMetadataNode variety = (IIOMetadataNode) rootnode.
                        getElementsByTagName("JPEGvariety").item(0);

                final IIOMetadataNode jfif = (IIOMetadataNode) variety.
                        getElementsByTagName("app0JFIF").item(0);
                jfif.setAttribute("resUnits", "1"); //dots per inch
                jfif.setAttribute("Xdensity", Integer.toString(resolution));
                jfif.setAttribute("Ydensity", Integer.toString(resolution));

                //dumpMetadata(iiometa);
                //DebugUtil.dumpNode(rootnode);
                iiometa.setFromTree(jpegmeta, rootnode);

                //dumpMetadata(iiometa);
            } else if (iiometa.isStandardMetadataFormatSupported()) {
                checkWritable(iiometa);

                final IIOMetadataNode rootnode = new IIOMetadataNode(stdmeta);

                final IIOMetadataNode imagedim = new IIOMetadataNode("Dimension");
                IIOMetadataNode child = new IIOMetadataNode("HorizontalPixelSize");
                final double effResolution = 1 / (UnitConv.in2mm(1) / resolution);
                child.setAttribute("value", Double.toString(effResolution));
                imagedim.appendChild(child);
                child = new IIOMetadataNode("VerticalPixelSize");
                child.setAttribute("value", Double.toString(effResolution));
                imagedim.appendChild(child);

                final IIOMetadataNode textNode = new IIOMetadataNode("Text");
                child = new IIOMetadataNode("TextEntry");
                child.setAttribute("keyword", "Software");
                child.setAttribute("value", "Barcode4J");
                child.setAttribute("encoding", "Unicode");
                child.setAttribute("language", "en");
                child.setAttribute("compression", "none");
                textNode.appendChild(child);

                rootnode.appendChild(imagedim);
                rootnode.appendChild(textNode);

                try {
                    iiometa.mergeTree(stdmeta, rootnode);
                } catch (Exception e1) {
                    LOGGER.log(Level.INFO, "Failure while merging MetaData-Tree. Trying to replace", e1);
                    try {
                        iiometa.setFromTree(stdmeta, rootnode);
                    } catch (Exception e2) {
                        //ignore metadata
                        LOGGER.log(Level.INFO, "Failure while replacing MetaData. Ignoring MetaData", e2);
                    }
                }
            }
        }

        return iiometa;
    }

    private void checkWritable(IIOMetadata iiometa) throws IOException {
        if (iiometa.isReadOnly()) {
            throw new IOException("Metadata is read-only. Cannot modify");
        }
    }
}
