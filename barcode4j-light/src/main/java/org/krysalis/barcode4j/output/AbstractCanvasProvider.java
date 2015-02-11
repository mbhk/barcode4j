/*
 * Copyright 2003-2004,2008 Jeremias Maerki.
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
package org.krysalis.barcode4j.output;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;

/**
 * Abstract base class for most CanvasProvider implementations.
 * 
 * @author Jeremias Maerki
 * @version 1.1
 */
public abstract class AbstractCanvasProvider implements CanvasProvider {

    /** the cached barcode dimensions */
    private BarcodeDimension bardim;

    /** the barcode orientation (0, 90, 180, 270) */
    private final Orientation orientation;
    
    /**
     * Main constructor.
     * @param orientation the orientation of the barcode
     */
    public AbstractCanvasProvider(Orientation orientation) {
        this.orientation = orientation;
    }
    
    /**
     * Help Constructor.
     * @param orientation orientation in degrees 
     */
    public AbstractCanvasProvider(int orientation) {
        this(Orientation.fromInt(orientation));
    }
    
    @Override
    public void establishDimensions(BarcodeDimension dim) {
        this.bardim = dim;
    }

    @Override
    public BarcodeDimension getDimensions() {
        return this.bardim;
    }
    
    @Override
    public Orientation getOrientation() {
        return this.orientation;
    }

    @Override
    public void deviceJustifiedText(String text,
            double x1, double x2, double y1,
            String fontName, double fontSize) {
        deviceText(text, x1, x2, y1, fontName, fontSize, TextAlignment.TA_JUSTIFY);
    }

    @Override
    public void deviceCenteredText(String text,
            double x1, double x2, double y1,
            String fontName, double fontSize) {
        deviceText(text, x1, x2, y1, fontName, fontSize, TextAlignment.TA_CENTER);
    }
}
