/*
 * Copyright 2002-2007 Jeremias Maerki or contributors to Barcode4J, as applicable
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
package org.krysalis.barcode4j.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.tools.Length;

import com.github.mbhk.barcode4j.Configuration;
import com.github.mbhk.barcode4j.ConfigurationException;

/**
 * Base class for most Avalon-Configurable barcode implementation proxies.
 *
 * @version $Id$
 */
public abstract class ConfigurableBarcodeGenerator
        implements BarcodeGenerator {

    /**
     * Contains all possible element names that may appear in barcode XML.
     */
    private static final List<String> elements;

    static {
        elements = new ArrayList<String>();
        //All barcode names
        elements.addAll(BarcodeUtil.getInstance().getClassResolver().getBarcodeNames());
        //All configuration element names
        elements.add("height");
        elements.add("module-width");
        elements.add("wide-factor");
        elements.add("quiet-zone");
        elements.add("vertical-quiet-zone");
        elements.add("checksum");
        elements.add("human-readable");
        elements.add("human-readable-font");
        elements.add("human-readable-size");
        elements.add("font-name");
        elements.add("font-size");
        elements.add("placement");
        elements.add("pattern");
        elements.add("display-start-stop");
        elements.add("display-checksum");
        elements.add("interchar-gap-width");
        elements.add("tall-bar-height");
        elements.add("short-bar-height");
        elements.add("track-height");
        elements.add("ascender-height");
        elements.add("baseline-alignment");
        elements.add("template");
        elements.add("group-separator");
        elements.add("check-digit-marker");
        elements.add("omit-brackets");
        elements.add("shape"); //DataMatrix
        elements.add("row-height"); //PDF417
        elements.add("columns"); //PDF417
        elements.add("min-columns"); //PDF417
        elements.add("max-columns"); //PDF417
        elements.add("min-rows"); //PDF417
        elements.add("max-rows"); //PDF417
        elements.add("ec-level"); //PDF417
        elements.add("width-to-height-ratio");
        elements.add("min-symbol-size"); //DataMatrix
        elements.add("max-symbol-size"); //DataMatrix
        elements.add("codesets"); //Code128
        elements.add("bearer-bar-width"); //ITF-14
        elements.add("bearer-box"); //ITF-14
    }

    /**
     * Proxy target. Barcode bean to configure.
     */
    protected AbstractBarcodeBean bean;

    public void configure(Configuration cfg) throws ConfigurationException {
        //Height (must be evaluated after the font size because of setHeight())
        Configuration c = cfg.getChild("height", false);
        if (c != null) {
            final Length h = new Length(c.getValue(), "mm");
            getBean().setHeight(h.getValueAsMillimeter());
        }

        //Quiet zone
        getBean().doQuietZone(cfg.getChild("quiet-zone").getAttributeAsBoolean("enabled", true));
        final String qzs = cfg.getChild("quiet-zone").getValue(null);
        if (qzs != null) {
            final Length qz = new Length(qzs, "mw");
            if (qz.getUnit().equalsIgnoreCase("mw")) {
                getBean().setQuietZone(qz.getValue() * getBean().getModuleWidth());
            } else {
                getBean().setQuietZone(qz.getValueAsMillimeter());
            }
        }

        //Vertical quiet zone
        final String qzvs = cfg.getChild("vertical-quiet-zone").getValue(null);
        if (qzvs != null) {
            final Length qz = new Length(qzvs, Length.INCH);
            if (qz.getUnit().equalsIgnoreCase("mw")) {
                getBean().setVerticalQuietZone(
                        qz.getValue() * getBean().getModuleWidth());
            } else {
                getBean().setVerticalQuietZone(
                        qz.getValueAsMillimeter());
            }
        }

        final Configuration hr = cfg.getChild("human-readable", false);
        if ((hr != null) && (hr.getChildren().length > 0)) {
            //Human-readable placement
            final String v = hr.getChild("placement").getValue(null);
            if (v != null) {
                getBean().setMsgPosition(HumanReadablePlacement.byName(v));
            }

            c = hr.getChild("font-size", false);
            if (c != null) {
                final Length fs = new Length(c.getValue());
                getBean().setFontSize(fs.getValueAsMillimeter());
            }

            getBean().setFontName(hr.getChild("font-name").getValue("Helvetica"));

            getBean().setPattern(hr.getChild("pattern").getValue(""));
        } else {
            //Legacy code for compatibility

            //Human-readable placement
            final String v = cfg.getChild("human-readable").getValue(null);
            if (v != null) {
                getBean().setMsgPosition(HumanReadablePlacement.byName(v));
            }

            c = cfg.getChild("human-readable-size", false);
            if (c != null) {
                final Length fs = new Length(c.getValue());
                getBean().setFontSize(fs.getValueAsMillimeter());
            }

            getBean().setFontName(cfg.getChild("human-readable-font").getValue("Helvetica"));
        }
    }

    public static final Collection<String> getBarcodeElements() {
        return Collections.unmodifiableCollection(elements);
    }

    /**
     * Provides access to the underlying barcode bean.
     *
     * @return the underlying barcode bean
     */
    public AbstractBarcodeBean getBean() {
        return this.bean;
    }

    @Override
    public void generateBarcode(CanvasProvider canvas, String msg) {
        getBean().generateBarcode(canvas, msg);
    }

    @Override
    public BarcodeDimension calcDimensions(String msg) {
        return getBean().calcDimensions(msg);
    }

    @Override
    public String getId() {
        return getBean().getId();
    }

    @Override
    public Collection<String> getAdditionalNames() {
        return getBean().getAdditionalNames();
    }
}
