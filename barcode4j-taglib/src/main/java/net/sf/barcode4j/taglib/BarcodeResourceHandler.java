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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ResourceWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeGeneratorProvider;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.w3c.dom.DocumentFragment;

/**
 *
 * @author mk
 */
public class BarcodeResourceHandler extends ResourceHandlerWrapper {

    public static final String LIBRARY_NAME = "barcode4j";
    private static final Logger LOGGER = Logger.getLogger(BarcodeResourceHandler.class.getName());

    private final ResourceHandler wrapped;

    public BarcodeResourceHandler(ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ResourceHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public Resource createResource(final String resourceName, final String libraryName) {
        final Resource resource = super.createResource(resourceName, libraryName);
        if (LIBRARY_NAME.equals(libraryName)) {
            return new BarcodeResource(resource);
        } else {
            return resource;
        }
    }

    @Override
    public boolean libraryExists(final String libraryName) {
        if (LIBRARY_NAME.equals(libraryName)) {
            return true;
        } else {
            return super.libraryExists(libraryName);
        }
    }

    private static class BarcodeResource extends ResourceWrapper {

        final Resource wrapped;

        public BarcodeResource(Resource resource) {
            wrapped = resource;
        }

        @Override
        public Resource getWrapped() {
            return wrapped;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            /*
             TODO: This method must be made much more robust!
             */

            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            Map<String, String> params = externalContext.getRequestParameterMap();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                if (params.get("orientation") != null && params.get("symbologie") != null && params.get("message") != null) {
                    LOGGER.info("handling barcode4j resourcerequest");
                    externalContext.setResponseContentType("image/svg+xml");
                    SVGCanvasProvider svgCanvasProvider = new SVGCanvasProvider(true, Orientation.valueOf(params.get("orientation")));
                    BarcodeGenerator gen = BarcodeGeneratorProvider.getInstance().getBarcodeGenerator(params.get("symbologie"));
                    gen.generateBarcode(svgCanvasProvider, params.get("message"));
                    DocumentFragment frag = svgCanvasProvider.getDOMFragment();

                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer trans = factory.newTransformer();
                    Source src = new javax.xml.transform.dom.DOMSource(frag);
                    Result res = new javax.xml.transform.stream.StreamResult(os);
                    trans.transform(src, res);
                }
                os.flush();
            } catch (BarcodeException ex) {
                Logger.getLogger(BarcodeResourceHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(BarcodeResourceHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(BarcodeResourceHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return new ByteArrayInputStream(os.toByteArray());
        }
    }
}
