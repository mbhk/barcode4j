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
package org.krysalis.barcode4j;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loading of implementation classes through SPI.
 *
 * @author mk
 * @version 1.1
 * @since 2.1.2
 */
public class BarcodeGeneratorProvider {

    private static BarcodeGeneratorProvider instance;
    private static final Logger LOGGER = Logger.getLogger(BarcodeGeneratorProvider.class.getName());
    private final ServiceLoader<BarcodeGenerator> loader;
    private final ConcurrentMap<String, Class<BarcodeGenerator>> barcodeGenerators;

    /**
     * Initialize this singleton.
     */
    private BarcodeGeneratorProvider() {
        this.barcodeGenerators = new ConcurrentHashMap<String, Class<BarcodeGenerator>>();
        loader = ServiceLoader.load(BarcodeGenerator.class);
        initialize();
    }

    /**
     * Gets the Provider.
     *
     * @return single instance
     */
    public static synchronized BarcodeGeneratorProvider getInstance() {
        if (instance == null) {
            instance = new BarcodeGeneratorProvider();
            LOGGER.log(Level.INFO, "Created new instance.");
        }
        return instance;
    }

    /**
     * Lists all names of available BarcodeGenerators.
     *
     * @return Set of available BarcodeGenerator names
     */
    public Collection<String> getAvailableBarcodeGenerators() {
        final Set<String> res = new TreeSet<String>();
        res.addAll(barcodeGenerators.keySet());
        return res;
    }

    /**
     * Instanciates a fresh copy of a BarcodeGenerator.
     *
     * @param id Name of Barcode ({@link #getAvailableBarcodeGenerators() })
     * @return a fresh copy
     * @throws BarcodeException if barcode with {@code id} is not available 
     */
    public BarcodeGenerator getBarcodeGenerator(String id) throws BarcodeException {
        final Class<BarcodeGenerator> clazz = barcodeGenerators.get(id);

        if (clazz == null) {
            throw new BarcodeException("BarcodeGenerator with " + id + " is not available.");
        }

        BarcodeGenerator res = null;
        try {
            res = clazz.newInstance();
        } catch (InstantiationException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return res;
    }

    /**
     * Loads all implementing Classes through ServiceLoader interface.
     */
    private void initialize() {
        final Iterator<BarcodeGenerator> iterator = loader.iterator();
        while (iterator.hasNext()) {
            try {
                final BarcodeGenerator bg = iterator.next();
                final String id = bg.getId();
                final Class<BarcodeGenerator> clazz = (Class<BarcodeGenerator>) bg.getClass();
                barcodeGenerators.put(id, clazz);
                final Collection<String> addIds = bg.getAdditionalNames();
                if (addIds != null) {
                    for (final String addId : addIds) {
                        barcodeGenerators.putIfAbsent(addId, clazz);
                    }
                }
            } catch (ServiceConfigurationError e) {
                LOGGER.log(Level.WARNING, "Failed to load a BarcodeGenerator service.", e);
            }
        }
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Available BarcodeGenerators: ");
            for (final Map.Entry<String, Class<BarcodeGenerator>> entrySet : barcodeGenerators.entrySet()) {
                final String key = entrySet.getKey();
                final Class<BarcodeGenerator> value = entrySet.getValue();
                LOGGER.log(Level.INFO, "{0} -> {1}", new Object[]{key, value.getName()});
            }
        }
    }
}
