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
package net.sf.barcode4j.taglib;

import javax.faces.application.ResourceDependencies;
import javax.faces.component.html.HtmlGraphicImage;
import org.krysalis.barcode4j.output.Orientation;

@ResourceDependencies({})
public class Barcode extends HtmlGraphicImage {

    public static final String BARCODE_COMPONENT_FAMILY = "net.sf.barcode4j.taglib";
    public static final String BARCODE_COMPONENT_TYPE = BARCODE_COMPONENT_FAMILY + ".Barcode";
    public static final String BARCODE_RENDERER = BARCODE_COMPONENT_FAMILY + ".BarcodeRenderer";

    enum PropertyKeys {

        MESSAGE, SYMBOLOGIE, ORIENTATION
    }

    public Barcode() {
        setRendererType(BARCODE_RENDERER);
    }

    @Override
    public String getFamily() {
        return BARCODE_COMPONENT_FAMILY;
    }

    public String getMessage() {
        return (String) getStateHelper().eval(PropertyKeys.MESSAGE);
    }

    public void setMessage(String message) {
        getStateHelper().put(PropertyKeys.MESSAGE, message);
    }

    public String getSymbologie() {
        return (String) getStateHelper().eval(PropertyKeys.SYMBOLOGIE);
    }

    public void setSymbologie(String symbologie) {
        getStateHelper().put(PropertyKeys.SYMBOLOGIE, symbologie);
    }

    public Orientation getOrientation() {
        return (Orientation) getStateHelper().eval(PropertyKeys.ORIENTATION, Orientation.ZERO);
    }

    public void setOrientation(Orientation orientation) {
        getStateHelper().put(PropertyKeys.ORIENTATION, orientation);
    }
}
