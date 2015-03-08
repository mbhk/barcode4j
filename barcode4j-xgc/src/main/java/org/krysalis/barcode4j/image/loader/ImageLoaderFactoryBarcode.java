/*
 * Copyright 2008,2010 Jeremias Maerki.
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

package org.krysalis.barcode4j.image.loader;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoaderFactory;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;

/**
 * Factory class for the ImageLoader for barcodes.
 * 
 * @version 1.2
 */
public class ImageLoaderFactoryBarcode extends AbstractImageLoaderFactory {
    private static final Logger LOGGER = Logger.getLogger(ImageLoaderFactoryBarcode.class.getName());

    /** MIME type for Barcode4J's barcode XML */
    public static final String MIME_TYPE = "application/x-barcode4j+xml";

    @Override
    public String[] getSupportedMIMETypes() {
        return new String[] {MIME_TYPE};
    }

    @Override
    public ImageFlavor[] getSupportedFlavors(String mime) {
        return new ImageFlavor[] {ImageBarcode.BARCODE_IMAGE_FLAVOR};
    }

    @Override
    public ImageLoader newImageLoader(ImageFlavor targetFlavor) {
        return new ImageLoaderBarcode(targetFlavor);
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("org.krysalis.barcode4j.BarcodeGenerator");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Barcode4J not in classpath", e);
        }
        return false;
    }
}
