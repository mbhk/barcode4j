/*
 * Copyright 2005,2010 Jeremias Maerki.
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

import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.fop.apps.FOPException;
import org.apache.fop.fo.FONode;
import org.apache.fop.fo.PropertyList;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.tools.ConfigurationUtil;
import org.krysalis.barcode4j.tools.MessageUtil;
import org.krysalis.barcode4j.tools.PageInfo;
import org.krysalis.barcode4j.tools.UnitConv;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import com.github.mbhk.barcode4j.Configuration;
import com.github.mbhk.barcode4j.ConfigurationException;

/**
 * Class representing bc:barcode extension element object.
 *
 * @author Jeremias Maerki
 * @version $Id$
 */
public class BarcodeElement extends BarcodeObj {
    private static final Logger LOGGER = Logger.getLogger(BarcodeElement.class.getName());

    public BarcodeElement(FONode parent) {
        super(parent);
    }

    @Override
    public void processNode(String elementName,
                            Locator locator,
                            Attributes attlist,
                            PropertyList propertyList) throws FOPException {
        super.processNode(elementName, locator, attlist, propertyList);
        init();
    }

    private void init() {
        createBasicDocument();
    }

    @Override
    public Point2D getDimension(Point2D view) {
        final Configuration cfg = ConfigurationUtil.buildConfiguration(this.doc);
        try {
            String msg = ConfigurationUtil.getMessage(cfg);
            msg = MessageUtil.unescapeUnicode(msg);

            final Orientation orientation = Orientation.fromInt(cfg.getAttributeAsInteger("orientation", 0));

            final BarcodeGenerator bargen = BarcodeUtil.getInstance().
                    createBarcodeGenerator(cfg);
            final String expandedMsg = FopVariableUtil.getExpandedMessage((PageInfo)null, msg);
            final BarcodeDimension bardim = bargen.calcDimensions(expandedMsg);
            final float w = (float)UnitConv.mm2pt(bardim.getWidthPlusQuiet(orientation));
            final float h = (float)UnitConv.mm2pt(bardim.getHeightPlusQuiet(orientation));
            return new Point2D.Float(w, h);
        } catch (ConfigurationException ce) {
            LOGGER.log(Level.INFO, "Error in Configuration", ce);
        } catch (BarcodeException be) {
            LOGGER.log(Level.INFO, "Error in Barcode", be);
        }
        return null;
    }
}
