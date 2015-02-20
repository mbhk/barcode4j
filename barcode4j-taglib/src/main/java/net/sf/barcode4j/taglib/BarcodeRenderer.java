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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.krysalis.barcode4j.output.Orientation;

/**
 *
 * @author mk
 */
public class BarcodeRenderer extends Renderer {

    private String constructUrl(FacesContext context, String message, String symbologie, Orientation orientation) throws UnsupportedEncodingException {
        final Resource resource = context.getApplication().getResourceHandler().createResource("barcode.svg", "barcode4j");
        final String resourcePath = resource.getRequestPath();

        final StringBuilder res = new StringBuilder(resourcePath);
        res.append("&")
                .append("orientation=")
                .append(orientation)
                .append("&")
                .append("symbologie=")
                .append(symbologie)
                .append("&")
                .append("message=")
                .append(URLEncoder.encode(message, "UTF-8"));

        return res.toString();
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final Barcode barcode = (Barcode) component;

        final Object message = barcode.getValue();
        final String symbologie = barcode.getSymbologie();
        final Orientation orientation = barcode.getOrientation();

        if (message == null) {
            return;
        }

        writer.startElement("img", barcode);
        writer.writeAttribute("id", barcode.getClientId(context), "id");
        writer.writeAttribute("alt", barcode.getAlt(), "alt");
        writer.writeAttribute("width", barcode.getWidth(), "width");
        writer.writeAttribute("height", barcode.getHeight(), "height");
        writer.writeAttribute("src",
                constructUrl(context, (String) message, symbologie, orientation), null);
        writer.endElement("img");
    }
}
