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

import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.impl.AbstractImage;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.tools.ConfigurationUtil;

import com.github.mbhk.barcode4j.Configuration;
import com.github.mbhk.barcode4j.ConfigurationException;

/**
 * Image implementation for barcodes. It implements the Image interface from Apache XML Graphic
 * Commons.
 */
public class ImageBarcode extends AbstractImage {

    /** Image flavor for Barcode4J barcodes */
    public static final ImageFlavor BARCODE_IMAGE_FLAVOR = new ImageFlavor("Barcode4J");

    static final Object MESSAGE = "Message";

    private final Configuration barcodeXML;
    private final BarcodeDimension bardim;

    /**
     * Main constructor.
     * @param info the image info object
     * @param barcodeXML the Configuration object containing the barcode XML
     * @param bardim the barcode dimensions
     */
    public ImageBarcode(ImageInfo info, Configuration barcodeXML, BarcodeDimension bardim) {
        super(info);
        this.barcodeXML = barcodeXML;
        this.bardim = bardim;
    }

    @Override
    public ImageFlavor getFlavor() {
        return BARCODE_IMAGE_FLAVOR;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    /**
     * Returns the barcode XML as Avalon Configuration object.
     * @return the barcode XML
     */
    public Configuration getBarcodeXML() {
        return this.barcodeXML;
    }

    /**
     * Returns the barcode dimensions.
     * @return the barcode dimensions
     */
    public BarcodeDimension getBarcodeDimension() {
        return this.bardim;
    }

    /**
     * Returns the barcode message.
     * @return the message
     * @throws ConfigurationException if an error occurs extracting the message from the
     *          configuration object
     */
    public String getMessage() throws ConfigurationException {
        String msg = (String)getInfo().getCustomObjects().get(MESSAGE);
        if (msg == null) {
            msg = ConfigurationUtil.getMessage(getBarcodeXML());
        }
        return msg;
    }
}
