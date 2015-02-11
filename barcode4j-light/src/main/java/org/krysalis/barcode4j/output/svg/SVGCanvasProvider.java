/*
 * Copyright 2002-2004,2006,2008 Jeremias Maerki.
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
package org.krysalis.barcode4j.output.svg;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.AbstractXMLGeneratingCanvasProvider;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;
import org.krysalis.barcode4j.output.Orientation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * Implementation that outputs to a W3C DOM.
 *
 * @author Jeremias Maerki
 * @author mk
 * @version 1.3
 */
public class SVGCanvasProvider extends AbstractXMLGeneratingCanvasProvider {

    /**
     * the SVG namespace
     */
    public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";

    private static final Logger LOGGER = Logger.getLogger(SVGCanvasProvider.class.getName());

    private final boolean useNamespace;
    private final String prefix;

    private final DOMImplementation domImpl;
    private Document doc;
    private Element detailGroup;

    /**
     * Main Constructor.
     *
     * @param domImpl DOMImplementation to use (JAXP default is used when this
     * is null)
     * @param useNamespace Controls whether namespaces should be used
     * @param namespacePrefix the namespace prefix to use, null for no prefix
     * @param orientation the barcode orientation
     */
    private SVGCanvasProvider(DOMImplementation domImpl, boolean useNamespace, String namespacePrefix,
            Orientation orientation) {
        super(orientation);
        if (namespacePrefix != null && namespacePrefix.isEmpty()) {
            throw new IllegalArgumentException("No empty prefix allowed.");
        }
        if (!useNamespace && namespacePrefix != null) {
            throw new IllegalArgumentException("No prefix allowed when namespaces are enabled.");
        }
        this.useNamespace = useNamespace;
        this.prefix = namespacePrefix;
        this.domImpl = initDOMImplementation(domImpl);
        init();
    }

    /**
     * Creates a new SVGCanvasProvider.
     *
     * @param domImpl DOMImplementation to use (JAXP default is used when this
     * is null)
     * @param useNamespace Controls whether namespaces should be used
     * @param orientation the barcode orientation
     */
    public SVGCanvasProvider(DOMImplementation domImpl, boolean useNamespace,
            Orientation orientation) {
        this(domImpl, useNamespace, null, orientation);
    }

    /**
     * Creates a new SVGCanvasProvider with namespaces enabled.
     *
     * @param domImpl DOMImplementation to use (JAXP default is used when this
     * is null)
     * @param namespacePrefix the namespace prefix to use, null for no prefix
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(DOMImplementation domImpl, String namespacePrefix,
            Orientation orientation) {
        this(domImpl, true, namespacePrefix, orientation);
    }

    /**
     * Creates a new SVGCanvasProvider.
     *
     * @param useNamespace Controls whether namespaces should be used
     * @param orientation the barcode orientation
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(boolean useNamespace, Orientation orientation) {
        this(null, useNamespace, orientation);
    }

    /**
     * Creates a new SVGCanvasProvider.
     *
     * @param useNamespace Controls whether namespaces should be used
     * @param orientation the barcode orientation
     * @throws BarcodeCanvasSetupException
     * @deprecated use {@link SVGCanvasProvider#SVGCanvasProvider(boolean, org.krysalis.barcode4j.output.Orientation) instead
     */
    @Deprecated
    public SVGCanvasProvider(boolean useNamespace, int orientation) {
        this(useNamespace, Orientation.fromInt(orientation));
    }

    /**
     * Creates a new SVGCanvasProvider with namespaces enabled.
     *
     * @param namespacePrefix the namespace prefix to use, null for no prefix
     * @param orientation the barcode orientation
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(String namespacePrefix, Orientation orientation) {
        this(null, true, namespacePrefix, orientation);
    }

    /**
     * Creates a new SVGCanvasProvider with default settings (with namespaces,
     * but without namespace prefix).
     *
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(Orientation orientation) {
        this(null, true, null, orientation);
    }

    private void init() {
        doc = createDocument();
        final Element svg = doc.getDocumentElement();

        detailGroup = createElement("g");
        detailGroup.setAttribute("fill", "black");
        detailGroup.setAttribute("stroke", "none");
        svg.appendChild(detailGroup);
    }

    private Element createElement(String localName) {
        Element el;
        if (isNamespaceEnabled()) {
            el = doc.createElementNS(SVG_NAMESPACE, getQualifiedName(localName));
        } else {
            el = doc.createElement(localName);
        }
        return el;
    }

    private Document createDocument() {
        if (isNamespaceEnabled()) {
            return this.domImpl.createDocument(
                    SVG_NAMESPACE, getQualifiedName("svg"), null);
        } else {
            return this.domImpl.createDocument(null, "svg", null);
        }
    }

    /**
     * Returns the DOM document containing the SVG barcode.
     *
     * @return the DOM document
     */
    public Document getDOM() {
        return this.doc;
    }

    /**
     * Returns the DOM fragment containing the SVG barcode.
     *
     * @return the DOM fragment
     */
    public DocumentFragment getDOMFragment() {
        final DocumentFragment frag = doc.createDocumentFragment();
        frag.appendChild(doc.importNode(doc.getFirstChild(), true));
        return frag;
    }

    @Override
    public void establishDimensions(BarcodeDimension dim) {
        super.establishDimensions(dim);
        final Orientation orientation = getOrientation();
        final Element svg = doc.getDocumentElement();
        svg.setAttribute("width", addUnit(dim.getWidthPlusQuiet(orientation)));
        svg.setAttribute("height", addUnit(dim.getHeightPlusQuiet(orientation)));
        final String w = getDecimalFormat().format(dim.getWidthPlusQuiet(orientation));
        final String h = getDecimalFormat().format(dim.getHeightPlusQuiet(orientation));
        svg.setAttribute("viewBox", "0 0 " + w + " " + h);
        String transform;
        switch (orientation) {
            case NINETY:
                transform = "rotate(-90) translate(-" + h + ")";
                break;
            case ONEHUNDRED_EIGHTY:
                transform = "rotate(-180) translate(-" + w + " -" + h + ")";
                break;
            case TWOHUNDRED_SEVENTY:
                transform = "rotate(-270) translate(0 -" + w + ")";
                break;
            default:
                transform = null;
        }
        if (transform != null) {
            detailGroup.setAttribute("transform", transform);
        }
    }

    @Override
    public void deviceFillRect(double x, double y, double w, double h) {
        final Element el = createElement("rect");
        el.setAttribute("x", getDecimalFormat().format(x));
        el.setAttribute("y", getDecimalFormat().format(y));
        el.setAttribute("width", getDecimalFormat().format(w));
        el.setAttribute("height", getDecimalFormat().format(h));
        detailGroup.appendChild(el);
    }

    @Override
    public void deviceText(String text, double x1, double x2, double y1,
            String fontName, double fontSize, TextAlignment textAlign) {
        final Element el = createElement("text");
        String anchor;
        double tx;
        if (textAlign == TextAlignment.TA_LEFT) {
            anchor = "start";
            tx = x1;
        } else if (textAlign == TextAlignment.TA_RIGHT) {
            anchor = "end";
            tx = x2;
        } else {
            anchor = "middle";
            tx = x1 + (x2 - x1) / 2;
        }
        el.setAttribute("font-family", fontName);
        el.setAttribute("font-size", getDecimalFormat().format(fontSize));
        el.setAttribute("text-anchor", anchor);
        el.setAttribute("x", getDecimalFormat().format(tx));
        el.setAttribute("y", getDecimalFormat().format(y1));
        if (textAlign == TextAlignment.TA_JUSTIFY) {
            el.setAttribute("textLength", getDecimalFormat().format(x2 - x1));
        }
        el.appendChild(doc.createTextNode(text));
        detailGroup.appendChild(el);
    }

    private DOMImplementation initDOMImplementation(DOMImplementation in) {
        try {
            DOMImplementation res = in;
            if (res == null) {
                final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                dbf.setValidating(false);
                final DocumentBuilder db = dbf.newDocumentBuilder();
                res = db.getDOMImplementation();
            }
            return res;
        } catch (ParserConfigurationException pce) {
            LOGGER.log(Level.SEVERE, "Error while creating SVG Document", pce);
            throw new IllegalStateException("Error while creating SVG Document", pce);
        }
    }

    /**
     * Indicates whether namespaces are enabled.
     *
     * @return true if namespaces are enabled
     */
    public boolean isNamespaceEnabled() {
        return this.useNamespace;
    }

    /**
     * Returns the namespace prefix
     *
     * @return the namespace prefix (may be null)
     */
    public String getNamespacePrefix() {
        return this.prefix;
    }

    /**
     * Constructs a fully qualified element name based on the namespace
     * settings.
     *
     * @param localName the local name
     * @return the fully qualified name
     */
    protected String getQualifiedName(String localName) {
        if (this.prefix == null) {
            return localName;
        } else {
            return this.prefix + ':' + localName;
        }
    }
}
