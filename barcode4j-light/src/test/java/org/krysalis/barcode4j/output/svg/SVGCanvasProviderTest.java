/*
 * Copyright 2015 mk.
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

import junit.framework.TestCase;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeGeneratorProvider;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.Orientation;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 *
 * @author mk
 */
public class SVGCanvasProviderTest extends TestCase {

    BarcodeGenerator gen;

    @Override
    protected void setUp() throws Exception {
        gen = BarcodeGeneratorProvider.getInstance().getBarcodeGenerator("ean-13");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    static String getStringFromDoc(org.w3c.dom.Document doc) {
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        DOMConfiguration cfg = lsSerializer.getDomConfig();
        cfg.setParameter("xml-declaration", false);
        cfg.setParameter("format-pretty-print", false);
        //cfg.setParameter("canonical-form", false);
        return lsSerializer.writeToString(doc);
    }

    /**
     * Test of getDOM method, of class SVGCanvasProvider.
     */
    public void testGetDOM() {
        System.out.println("getDOM - default");
        SVGCanvasProvider instance = new SVGCanvasProvider(Orientation.ZERO);
        String expResult = "<svg xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"black\" stroke=\"none\"/></svg>";
        Document result = instance.getDOM();
        assertEquals(expResult, getStringFromDoc(result));
        
        System.out.println("getDOM - no namespace");
        instance = new SVGCanvasProvider(false, Orientation.ZERO);
        expResult = "<svg><g fill=\"black\" stroke=\"none\"/></svg>";
        result = instance.getDOM();
        assertEquals(expResult, getStringFromDoc(result));
        
        System.out.println("getDOM - with namespace and prefix");
        instance = new SVGCanvasProvider("svg", Orientation.ZERO);
        expResult = "<svg:svg xmlns:svg=\"http://www.w3.org/2000/svg\"><svg:g fill=\"black\" stroke=\"none\"/></svg:svg>";
        result = instance.getDOM();
        assertEquals(expResult, getStringFromDoc(result));
    }

}
