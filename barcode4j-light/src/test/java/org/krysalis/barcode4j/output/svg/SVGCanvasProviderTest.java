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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.output.Orientation;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mk
 */
public class SVGCanvasProviderTest {

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
    @Test
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

        try {
            System.out.println("getDOM - without namespace but prefix");
            instance = new SVGCanvasProvider(null, false, "foo", Orientation.ZERO);
            fail("not allowed combination of parameters");
        } catch (IllegalArgumentException e) {
        }

        try {
            System.out.println("getDOM - with empty prefix");
            instance = new SVGCanvasProvider(null, true, "", Orientation.ZERO);
            fail("not allowed empty prefix");
        } catch (IllegalArgumentException e) {
        }
    }

    private Document fromFragment(DocumentFragment docFrag) throws Exception {
        final DOMImplementation impl = getDomImpl();
        Document res = impl.createDocument(null, "test", null);
        Node importNode = res.importNode(docFrag, true);
        Node e = res.getDocumentElement();
        e.appendChild(importNode);
        return res;
    }

    @Test
    public void testGetDOMFragment() throws Exception {
        System.out.println("getDOMFragment - default");
        SVGCanvasProvider instance = new SVGCanvasProvider(Orientation.ZERO);
        instance.establishDimensions(new BarcodeDimension(110, 110));
        instance.deviceFillRect(0, 0, 100, 100);
        String expResult = "<test><svg xmlns=\"http://www.w3.org/2000/svg\" height=\"110mm\" viewBox=\"0 0 110 110\" width=\"110mm\"><g fill=\"black\" stroke=\"none\"><rect height=\"100\" width=\"100\" x=\"0\" y=\"0\"/></g></svg></test>";
        DocumentFragment result = instance.getDOMFragment();
        assertEquals(expResult, getStringFromDoc(fromFragment(result)));
    }

    private DOMImplementation getDomImpl() throws Exception {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setValidating(false);
        final DocumentBuilder db = dbf.newDocumentBuilder();

        return db.getDOMImplementation();
    }
}
