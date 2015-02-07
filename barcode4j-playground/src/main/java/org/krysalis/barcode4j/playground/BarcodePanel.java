/*
 * Copyright 2002-2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.playground;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

/**
 * A GUI Panel.
 *
 * The Barcode is directly drawn in {@link #paintComponent(java.awt.Graphics) } 
 *
 * @version 2.1.2
 */
public class BarcodePanel extends JPanel {

    private static final long serialVersionUID = 3864384667358207962L;
    private static final Logger LOGGER = Logger.getLogger(BarcodePanel.class.getName());

    private transient BarcodeGenerator bargen = null;
    private String barcodeName = null;
    private String msg = "";
    private int orientation = 0;

    /**
     * Sets the Barcode type-name and tries to load the corresponding
     * implementation.
     *
     * @param name Name of barcode type
     */
    public void setBarcodeName(String name) {
        this.barcodeName = name;
        DefaultConfiguration cfg = new DefaultConfiguration(this.barcodeName);
        DefaultConfiguration child = new DefaultConfiguration("human-readable-font");
        child.setValue("Tahoma");
        cfg.addChild(child);

        try {
            BarcodeGenerator gen
                    = BarcodeUtil.getInstance().createBarcodeGenerator(cfg);
            setBarcode(gen);
        } catch (ConfigurationException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (BarcodeException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sets the unencoded message of the barcode.
     *
     * @param message unencoded message
     */
    public void setMessage(String message) {
        this.msg = message;
        repaint();
    }

    /**
     * Sets orientation of Barcode.
     *
     * @param orientation rotation in degrees
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Rectangle rect = new Rectangle(30, 30, getWidth() - 60, getHeight() - 60);
        try {
            paintBarcode(graphics, rect);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Barcode not paintable.", e);
            paintError(graphics, e);
        }
    }

    /**
     * Changes barcode implementation and repaints component.
     *
     * @param bargen Barcode instance
     */
    private void setBarcode(BarcodeGenerator bargen) {
        this.bargen = bargen;
        repaint();
    }

    /**
     * Returns current instance of barcode.
     *
     * If no instance is present but we have a type-name, trying to get a
     * fresh instance (bargen is transient).
     *
     * @return Barcode instance
     */
    private BarcodeGenerator getBarcode() {
        if (bargen == null && barcodeName != null) {
            setBarcodeName(barcodeName);
        }
        return bargen;
    }

    /**
     * Tries to paint barcode.
     *
     * Drawing is done on temporary BufferedImage to preserve graphics state
     * in case of an error.
     *
     * @param graphics target drawing area
     * @param rect usable painting area
     */
    private void paintBarcode(Graphics graphics, Rectangle rect) {
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Dimension dim = rect.getSize();
        final Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        CanvasProvider provider = new Java2DCanvasProvider(g2d, orientation);

        AffineTransform baktrans = g2d.getTransform();
        g2d.translate(rect.getX(), rect.getY());

        BarcodeDimension barDim = getBarcode().calcDimensions(msg);
        LOGGER.log(Level.INFO, "bardim: {0}", barDim);
        double sc1 = dim.getWidth() / barDim.getWidthPlusQuiet(provider.getOrientation());
        double sc2 = dim.getHeight() / barDim.getHeightPlusQuiet(provider.getOrientation());
        g2d.scale(sc1, sc2);

        g2d.setColor(Color.black);

        getBarcode().generateBarcode(provider, msg);
        g2d.setTransform(baktrans);
        graphics.drawImage(img, 0, 0, this);
    }

    /**
     * Paints the error message.
     *
     * @param g target drawing area
     * @param e exception to show
     */
    private void paintError(Graphics g, Exception e) {
        String exceptionMsg = e.getMessage();
        g.setColor(Color.RED);
        g.setFont(new Font("sans-serif", Font.BOLD, 13));
        g.drawString(exceptionMsg, getWidth() / 2 - g.getFontMetrics().stringWidth(exceptionMsg) / 2,
                getHeight() / 2 + g.getFontMetrics().getHeight() / 2);
    }
}
