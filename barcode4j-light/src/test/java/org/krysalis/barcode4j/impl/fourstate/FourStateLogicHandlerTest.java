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
package org.krysalis.barcode4j.impl.fourstate;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.krysalis.barcode4j.impl.HeightVariableBarcodeBean;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProviderTest;

/**
 *
 * @author mk
 */
public class FourStateLogicHandlerTest {

    HeightVariableBarcodeBean bean;
    Canvas canvas;
    SVGCanvasProvider svg;

    @Before
    public void setUp() {
        svg = new SVGCanvasProvider(Orientation.ZERO);
        canvas = new Canvas(svg);
        bean = new USPSIntelligentMailBean();
        bean.setModuleWidth(1);
    }

    /**
     * Test of addBar method, of class FourStateLogicHandler.
     */
    @Test
    public void testAddBar() {
        System.out.println("addBar");
        FourStateLogicHandler instance = new FourStateLogicHandler(bean, canvas);
        String expected = "<svg xmlns=\"http://www.w3.org/2000/svg\"><g fill=\"black\" stroke=\"none\"><rect height=\"1.27\" width=\"1\" x=\"0\" y=\"1.9812\"/><rect height=\"2.54\" width=\"1\" x=\"1.635\" y=\"0.7112\"/><rect height=\"2.54\" width=\"1\" x=\"3.27\" y=\"1.9812\"/><rect height=\"3.81\" width=\"1\" x=\"4.905\" y=\"0.7112\"/><rect height=\"1.27\" width=\"1\" x=\"6.54\" y=\"1.9812\"/><rect height=\"2.54\" width=\"1\" x=\"8.175\" y=\"0.7112\"/><rect height=\"2.54\" width=\"1\" x=\"9.81\" y=\"1.9812\"/><rect height=\"3.81\" width=\"1\" x=\"11.445\" y=\"0.7112\"/></g></svg>";

        // parameter black is ignored. every bar will be black
        boolean black = false;
        instance.addBar(black, 0);
        instance.addBar(black, 1);
        instance.addBar(black, 2);
        instance.addBar(black, 3);
        black = true;
        instance.addBar(black, 0);
        instance.addBar(black, 1);
        instance.addBar(black, 2);
        instance.addBar(black, 3);
        assertEquals(expected, SVGCanvasProviderTest.getStringFromDoc(svg.getDOM()));
    }

    @Test(expected = RuntimeException.class)
    public void testAddBarBadHeight() {
        FourStateLogicHandler instance = new FourStateLogicHandler(bean, canvas);
        instance.addBar(true, -1);
    }
}
