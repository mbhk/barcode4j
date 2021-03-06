/*
 * Copyright 2005-2006,2010 Jeremias Maerki.
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
package org.krysalis.barcode4j.fop;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.fop.area.PageViewport;
import org.apache.fop.render.Graphics2DAdapter;
import org.apache.fop.render.Graphics2DImagePainter;
import org.apache.fop.render.ImageAdapter;
import org.apache.fop.render.Renderer;
import org.apache.fop.render.RendererContext;
import org.apache.fop.render.RendererContextConstants;
import org.apache.fop.render.XMLHandler;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.PSImageUtils;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.ConfigurationUtil;
import org.krysalis.barcode4j.tools.UnitConv;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import com.github.mbhk.barcode4j.Configuration;

/**
 * XMLHandler for Apache FOP that handles the Barcode XML by converting it to
 * SVG or by rendering it directly to the output format.
 *
 * @author Jeremias Maerki
 * @version $Id$
 */
public class BarcodeXMLHandler implements XMLHandler, RendererContextConstants {

    private static final boolean DEBUG = false;

    /** The context constant for the PostScript generator that is being used to drawn into. */
    private static final String PS_GENERATOR = "psGenerator";

    @Override
    public void handleXML(RendererContext context,
            Document doc, String ns) throws Exception {
        final Configuration cfg = ConfigurationUtil.buildConfiguration(doc);
        final String msg = ConfigurationUtil.getMessage(cfg);
        if (DEBUG) {
            System.out.println("Barcode message: " + msg);
        }
        final String renderMode = cfg.getAttribute("render-mode", "native");
        final Orientation orientation = Orientation.fromInt(cfg.getAttributeAsInteger("orientation", 0));

        final PageViewport page = (PageViewport)context.getProperty(PAGE_VIEWPORT);

        final BarcodeGenerator bargen = BarcodeUtil.getInstance().
                createBarcodeGenerator(cfg);
        final String expandedMsg = FopVariableUtil.getExpandedMessage(
                page, msg);

        boolean handled = false;
        String effRenderMode = renderMode;
        if ("native".equals(renderMode)) {
            if (context.getProperty(PS_GENERATOR) != null) {
                renderUsingEPS(context, bargen, expandedMsg, orientation);
                effRenderMode = "native";
                handled = true;
            }
        } else if ("g2d".equals(renderMode)) {
            handled = renderUsingGraphics2D(context, bargen, expandedMsg, orientation);
            if (handled) {
                effRenderMode = "g2d";
            }
        } else if ("bitmap".equals(renderMode)) {
            handled = renderUsingBitmap(context, bargen, expandedMsg, orientation);
            if (handled) {
                effRenderMode = "bitmap";
            }
        }
        if (!handled) {
            //Convert the Barcode XML to SVG and let it render through
            //an SVG handler
            convertToSVG(context, bargen, expandedMsg, orientation);
            effRenderMode = "svg";
        }
        if (DEBUG) {
            System.out.println("Effective render mode: " + effRenderMode);
        }
    }

    private void renderUsingEPS(RendererContext context, BarcodeGenerator bargen,
                String msg, Orientation orientation) throws IOException {
        final PSGenerator gen = (PSGenerator)context.getProperty(PS_GENERATOR);
        final ByteArrayOutputStream baout = new ByteArrayOutputStream(1024);
        final EPSCanvasProvider canvas = new EPSCanvasProvider(baout, orientation);
        bargen.generateBarcode(canvas, msg);
        canvas.finish();

        final BarcodeDimension barDim = canvas.getDimensions();
        final float bw = (float)UnitConv.mm2pt(barDim.getWidthPlusQuiet(orientation));
        final float bh = (float)UnitConv.mm2pt(barDim.getHeightPlusQuiet(orientation));

        final float width = (Integer)context.getProperty(WIDTH) / 1000f;
        final float height = (Integer)context.getProperty(HEIGHT) / 1000f;
        final float x = (Integer)context.getProperty(XPOS) / 1000f;
        final float y = (Integer)context.getProperty(YPOS) / 1000f;

        if (DEBUG) {
            System.out.println(" --> EPS");
        }
        PSImageUtils.renderEPS(new java.io.ByteArrayInputStream(baout.toByteArray()),
                "Barcode:" + msg,
                new Rectangle2D.Float(x, y, width, height),
                new Rectangle2D.Float(0, 0, bw, bh),
                gen);
    }

    private boolean renderUsingGraphics2D(RendererContext context,
            final BarcodeGenerator bargen,
            final String msg, final Orientation orientation) throws IOException {

        final Graphics2DAdapter g2dAdapter = context.getRenderer().getGraphics2DAdapter();
        if (g2dAdapter == null) {
            //We can't paint the barcode
            return false;
        } else {
            final BarcodeDimension barDim = bargen.calcDimensions(msg);

            // get the 'width' and 'height' attributes of the barcode
            final int w = (int)Math.ceil(UnitConv.mm2pt(barDim.getWidthPlusQuiet())) * 1000;
            final int h = (int)Math.ceil(UnitConv.mm2pt(barDim.getHeightPlusQuiet())) * 1000;

            final Graphics2DImagePainter painter = new Graphics2DImagePainter() {

                @Override
                public void paint(Graphics2D g2d, Rectangle2D area) {
                    Java2DCanvasProvider canvas = new Java2DCanvasProvider(null, orientation);
                    canvas.setGraphics2D(g2d);
                    g2d.scale(area.getWidth() / barDim.getWidthPlusQuiet(),
                            area.getHeight() / barDim.getHeightPlusQuiet());
                    bargen.generateBarcode(canvas, msg);
                }

                @Override
                public Dimension getImageSize() {
                    return new Dimension(w, h);
                }

            };

            if (DEBUG) {
                System.out.println(" --> Java2D");
            }
            g2dAdapter.paintImage(painter,
                    context, (Integer) context.getProperty("xpos")
                    , (Integer) context.getProperty("ypos")
                    , (Integer) context.getProperty("width")
                    , (Integer) context.getProperty("height"));
            return true;
        }
    }

    private boolean renderUsingBitmap(RendererContext context,
            final BarcodeGenerator bargen,
            final String msg, final Orientation orientation) throws IOException {
        final ImageAdapter imgAdapter = context.getRenderer().getImageAdapter();
        if (imgAdapter == null) {
            //We can't paint the barcode
            return false;
        } else {
            final BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    300, BufferedImage.TYPE_BYTE_BINARY, false, orientation);
            bargen.generateBarcode(canvas, msg);

            if (DEBUG) {
                System.out.println(" --> Bitmap");
            }
            imgAdapter.paintImage(canvas.getBufferedImage(),
                    context,
                    (Integer)context.getProperty("xpos"),
                    (Integer)context.getProperty("ypos"),
                    (Integer)context.getProperty("width"),
                    (Integer)context.getProperty("height"));
            return true;
        }
    }

    /**
     * Converts the barcode XML to SVG.
     * @param context the renderer context
     * @param bargen the barcode generator
     * @param msg the barcode message
     * @throws BarcodeCanvasSetupException In case of an error while generating the barcode
     */
    private void convertToSVG(RendererContext context,
            BarcodeGenerator bargen, String msg, Orientation orientation)
                throws BarcodeCanvasSetupException {
        final DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();

        final SVGCanvasProvider canvas = new SVGCanvasProvider(impl, true, orientation);
        bargen.generateBarcode(canvas, msg);
        final Document svg = canvas.getDOM();

        //Call the renderXML() method of the renderer to render the SVG
        if (DEBUG) {
            System.out.println(" --> SVG");
        }
        context.getRenderer().renderXML(context,
                svg, SVGDOMImplementation.SVG_NAMESPACE_URI);
    }

    public String getMimeType() {
        return XMLHandler.HANDLE_ALL;
    }

    @Override
    public String getNamespace() {
        return BarcodeElementMapping.NAMESPACE;
    }

    @Override
    public boolean supportsRenderer(Renderer renderer) {
        return renderer.getGraphics2DAdapter() != null;
    }
}
