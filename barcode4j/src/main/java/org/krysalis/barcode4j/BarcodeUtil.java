/*
 * Copyright 2002-2004 Jeremias Maerki.
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
package org.krysalis.barcode4j;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.w3c.dom.DocumentFragment;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.krysalis.barcode4j.output.Orientation;

/**
 * This is a convenience class to generate barcodes. It is implemented as
 * Singleton to cache the BarcodeClassResolver. However, the class also contains
 * a set of static methods which you can use of you manage your own
 * BarcodeClassResolver.
 *
 * @author Jeremias Maerki
 * @version $Id$
 */
public class BarcodeUtil {

    private static BarcodeUtil instance = null;
    private static final Logger LOGGER = Logger.getLogger(BarcodeUtil.class.getName());

    private final BarcodeClassResolver classResolver = new DefaultBarcodeClassResolver();

    /**
     * Creates a new BarcodeUtil object. This constructor is protected because
     * this class is designed as a singleton.
     */
    protected BarcodeUtil() {
        //nop
    }

    /**
     * Returns the default instance of this class.
     *
     * @return the singleton
     */
    public static BarcodeUtil getInstance() {
        if (instance == null) {
            instance = new BarcodeUtil();
        }
        return instance;
    }

    /**
     * Returns the class resolver used by this class.
     *
     * @return a BarcodeClassResolver instance
     */
    public BarcodeClassResolver getClassResolver() {
        return this.classResolver;
    }

    private static Class resolve(BarcodeClassResolver resolver, String type) {
        Class res = null;
        try {
            res = resolver.resolve(type);
        } catch (ClassNotFoundException cnfe) {
            LOGGER.log(Level.INFO, null, cnfe);
        }
        return res;
    }

    private static BarcodeCfgAndClass resolveBarcodeCfgAndClass(Configuration cfg, BarcodeClassResolver resolver) throws BarcodeException {
        final BarcodeCfgAndClass res = new BarcodeCfgAndClass();
        //First, check Configuration directly
        res.clazz = resolve(resolver, cfg.getName());
        res.cfg = null;
        if (res.clazz == null) {
            //Second, check children
            final Configuration[] children = cfg.getChildren();
            if (children.length == 0) {
                throw new BarcodeException("Barcode configuration element expected");
            }

            //Find barcode config element
            boolean found = false;
            for (int i = 0; i < children.length && !found; ++i) {
                res.cfg = children[i];
                res.clazz = resolve(resolver, res.cfg.getName());
                found = res.clazz != null;
            }
        }
        return res;
    }

    private static class BarcodeCfgAndClass {
        private Class clazz = null;
        private Configuration cfg = null;
    }

    private static BarcodeGenerator instantiateBarcode(Class clazz) throws BarcodeException {
        if (clazz == null) {
            throw new BarcodeException(
                    "No known barcode configuration element found");
        }
        final BarcodeGenerator res;
        try {
            //Instantiate the BarcodeGenerator
            res = (BarcodeGenerator) clazz.newInstance();
        } catch (IllegalAccessException ia) {
            throw new RuntimeException("Problem while instantiating a barcode"
                    + " generator: " + ia.getMessage());
        } catch (InstantiationException ie) {
            throw new BarcodeException("Error instantiating a barcode generator: "
                    + clazz.getName());
        }
        return res;
    }

    private static void configure(BarcodeGenerator gen, Configuration configuration) throws BarcodeException {
        try {
            ContainerUtil.configure(gen, configuration);
        } catch (ConfigurationException iae) {
            throw new BarcodeException("Cannot configure barcode generator: " + iae.getMessage());
        }
        try {
            ContainerUtil.initialize(gen);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot initialize barcode generator.", e);
        }
    }

    /**
     * Creates a BarcoderGenerator.
     *
     * @param cfg Configuration object that specifies the barcode to produce.
     * @param classResolver The BarcodeClassResolver to use for lookup of
     * barcode implementations.
     * @return the newly instantiated BarcodeGenerator
     * @throws BarcodeException if setting up a BarcodeGenerator fails
     */
    public static BarcodeGenerator createBarcodeGenerator(Configuration cfg,
            BarcodeClassResolver classResolver)
            throws BarcodeException {
        final BarcodeCfgAndClass cfgAndClass = resolveBarcodeCfgAndClass(cfg, classResolver);
        final BarcodeGenerator gen = instantiateBarcode(cfgAndClass.clazz);
        configure(gen, cfgAndClass.cfg == null ? cfg : cfgAndClass.cfg);
        return gen;
    }

    /**
     * Creates a BarcoderGenerator.
     *
     * @param cfg Configuration object that specifies the barcode to produce.
     * @return the newly instantiated BarcodeGenerator
     * @throws BarcodeException if setting up a BarcodeGenerator fails
     */
    public BarcodeGenerator createBarcodeGenerator(Configuration cfg)
            throws BarcodeException {
        return createBarcodeGenerator(cfg, this.classResolver);
    }

    /**
     * Convenience method to create an SVG barocde as a DOM fragment.
     *
     * @param cfg Configuration object that specifies the barcode to produce.
     * @param msg message to encode.
     * @return the requested barcode as an DOM fragment (SVG format)
     * @throws BarcodeException if setting up a BarcodeGenerator fails
     */
    public DocumentFragment generateSVGBarcode(Configuration cfg,
            String msg)
            throws BarcodeException {
        final BarcodeGenerator gen = createBarcodeGenerator(cfg);
        final SVGCanvasProvider svg = new SVGCanvasProvider(false, Orientation.ZERO);

        //Create Barcode and render it to SVG
        gen.generateBarcode(svg, msg);

        return svg.getDOMFragment();
    }
}
