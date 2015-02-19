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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    private static final Logger LOGGER = Logger.getLogger(BarcodeRenderer.class.getName());
    
    private String constructUrl(FacesContext context, String message, String symbologie, Orientation orientation) throws UnsupportedEncodingException {
        Resource resource = context.getApplication().getResourceHandler().createResource("barcode.svg", "barcode4j");
        String resourcePath = resource.getRequestPath();
        
        StringBuilder res = new StringBuilder(resourcePath);
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
        LOGGER.log(Level.INFO, "encodeEnd {0}", component);
        ResponseWriter writer = context.getResponseWriter();
        Barcode barcode = (Barcode) component;
        
        String message = barcode.getMessage();
        String symbologie = barcode.getSymbologie();
        Orientation orientation = barcode.getOrientation();
        
        writer.startElement("img", barcode);
        writer.writeAttribute("id", barcode.getClientId(context), "id");
        writer.writeAttribute("alt", message, "alt");
        
        String url = constructUrl(context, message, symbologie, orientation);
        // TODO Construct URL
        writer.writeAttribute("src", url, null);
        writer.endElement("img");
    }
}
