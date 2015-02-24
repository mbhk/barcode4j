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
package org.krysalis.barcode4j.impl.pdf417;

import java.awt.Dimension;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.DefaultTwoDimCanvasLogicHandler;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProviderTest;

/**
 *
 * @author mk
 */
public class PDF417LogicImplTest {

    private SVGCanvasProvider svg;

    @Before
    public void setUp() {
        svg = new SVGCanvasProvider(Orientation.ZERO);
    }

    /**
     * Test of generateBarcodeLogic method, of class PDF417LogicImpl.
     */
    @Test
    public void testGenerateBarcodeLogic() {
        System.out.println("generateBarcodeLogic");
        PDF417Bean pdf417Bean = new PDF417Bean();
        TwoDimBarcodeLogicHandler logic = new DefaultTwoDimCanvasLogicHandler(pdf417Bean, new Canvas(svg));
        String msg = "url(data:;encoding=UTF-8,Barcode4J)";

        PDF417LogicImpl.generateBarcodeLogic(logic, msg, pdf417Bean);
        assertTrue("sth long has been printed...", SVGCanvasProviderTest.getStringFromDoc(svg.getDOM()).length() > 200);
    }

    /**
     * Test of determineDimensions method, of class PDF417LogicImpl.
     */
    @Test
    public void testDetermineDimensions() {
        System.out.println("determineDimensions");
        PDF417Bean pdf417Bean = new PDF417Bean();
        int sourceCodeWords = 9;
        Dimension expResult = new Dimension(2, 6);
        Dimension result = PDF417LogicImpl.determineDimensions(pdf417Bean, sourceCodeWords);
        assertEquals(expResult, result);
        pdf417Bean.setColumns(4);
        expResult = new Dimension(4, 3);
        result = PDF417LogicImpl.determineDimensions(pdf417Bean, sourceCodeWords);
        assertEquals(expResult, result);
    }
}
