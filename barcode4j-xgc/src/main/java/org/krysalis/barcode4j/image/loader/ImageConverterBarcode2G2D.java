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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;
import org.krysalis.barcode4j.tools.PageInfo;
import org.krysalis.barcode4j.tools.VariableUtil;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageConverter;
import org.apache.xmlgraphics.image.loader.impl.ImageGraphics2D;
import org.apache.xmlgraphics.java2d.Graphics2DImagePainter;
import org.krysalis.barcode4j.output.Orientation;

/**
 * This ImageConverter converts barcodes to Java2D.
 * 
 * @version 1.1
 */
public class ImageConverterBarcode2G2D extends AbstractImageConverter {

    @Override
    public Image convert(Image src, Map hints) throws ImageException {
        checkSourceFlavor(src);
        final ImageBarcode barcodeImage = (ImageBarcode)src;

        final Configuration cfg = barcodeImage.getBarcodeXML();
        final Orientation orientation = Orientation.fromInt(
                cfg.getAttributeAsInteger("orientation", 0));

        try {
            final String msg = barcodeImage.getMessage();
            final PageInfo pageInfo = PageInfo.fromProcessingHints(hints);
            final String expandedMsg = VariableUtil.getExpandedMessage(pageInfo, msg);

            final BarcodeGenerator bargen = BarcodeUtil.getInstance().
                        createBarcodeGenerator(cfg);

            final Graphics2DImagePainter painter = new Graphics2DImagePainterBarcode(
                    barcodeImage, bargen, expandedMsg, orientation);

            final ImageGraphics2D g2dImage = new ImageGraphics2D(src.getInfo(), painter);
            return g2dImage;
        } catch (ConfigurationException ce) {
            throw new ImageException("Error in Barcode XML", ce);
        } catch (BarcodeException be) {
            throw new ImageException("Error while converting barcode to Java2D", be);
        }
    }

    @Override
    public ImageFlavor getSourceFlavor() {
        return ImageBarcode.BARCODE_IMAGE_FLAVOR;
    }

    @Override
    public ImageFlavor getTargetFlavor() {
        return ImageFlavor.GRAPHICS2D;
    }

    private static class Graphics2DImagePainterBarcode implements Graphics2DImagePainter {

        private ImageBarcode barcodeImage;
        private BarcodeGenerator bargen;
        private Orientation orientation;
        private String msg;

        public Graphics2DImagePainterBarcode(ImageBarcode barcodeImage, BarcodeGenerator bargen,
                String msg, Orientation orientation) {
            this.barcodeImage = barcodeImage;
            this.bargen = bargen;
            this.msg = msg;
            this.orientation = orientation;
        }

        @Override
        public Dimension getImageSize() {
            return barcodeImage.getSize().getDimensionMpt();
        }

        @Override
        public void paint(Graphics2D g2d, Rectangle2D area) {
            final double w = area.getWidth();
            final double h = area.getHeight();

            //Fit in paint area and
            //set up for the CanvasProvider's internal coordinate system (mm-based)
            g2d.translate(area.getX(), area.getY());
            final BarcodeDimension bardim = barcodeImage.getBarcodeDimension();
            final double bsx = w / bardim.getWidthPlusQuiet(orientation);
            final double bsy = h / bardim.getHeightPlusQuiet(orientation);
            g2d.scale(bsx, bsy);

            g2d.setColor(Color.BLACK);
            final Java2DCanvasProvider canvas = new Java2DCanvasProvider(g2d, orientation);
            bargen.generateBarcode(canvas, msg);
        }
    }
}
