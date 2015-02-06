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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mk
 */
public class BarcodeGeneratorProvider {

    private static BarcodeGeneratorProvider instance;
    private static final Logger LOGGER = Logger.getLogger(BarcodeGeneratorProvider.class.getName());
    private final ServiceLoader<BarcodeGenerator> loader;
    private final Map<String, Class<BarcodeGenerator>> barcodeGenerators;

    private BarcodeGeneratorProvider() {
        this.barcodeGenerators = new HashMap<String, Class<BarcodeGenerator>>();
        loader = ServiceLoader.load(BarcodeGenerator.class);
        initialize();
    }

    public static synchronized BarcodeGeneratorProvider getInstance() {
        if (instance == null) {
            instance = new BarcodeGeneratorProvider();
            LOGGER.log(Level.INFO, "Created new instance.");
        }
        return instance;
    }

    public Collection<String> getAvailableBarcodeGenerators() {
        HashSet<String> res = new HashSet<String>();
        res.addAll(barcodeGenerators.keySet());
        return res;
    }

    public BarcodeGenerator getBarcodeGenerator(String id) throws BarcodeException {
        Class<BarcodeGenerator> clazz = barcodeGenerators.get(id);

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

    private void initialize() {
        Iterator<BarcodeGenerator> iterator = loader.iterator();
        while (iterator.hasNext()) {
            BarcodeGenerator bg = iterator.next();
            String id = bg.getId();
            Class<BarcodeGenerator> clazz = (Class<BarcodeGenerator>) bg.getClass();
            barcodeGenerators.put(id, clazz);
            Collection<String> addIds = bg.getAdditionalNames();
            if (addIds != null) {
                for (String addId : addIds) {
                    barcodeGenerators.putIfAbsent(addId, clazz);
                }
            }
        }
        if(LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Available BarcodeGenerators: ");
            for (Map.Entry<String, Class<BarcodeGenerator>> entrySet : barcodeGenerators.entrySet()) {
                String key = entrySet.getKey();
                Class<BarcodeGenerator> value = entrySet.getValue();
                LOGGER.log(Level.INFO, "{0} -> {1}", new Object[]{key, value.getName()});
            }
        }
    }
}
