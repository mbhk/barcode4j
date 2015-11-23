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
package org.krysalis.barcode4j.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.krysalis.barcode4j.BarcodeException;

import com.github.mbhk.barcode4j.Configuration;
import com.github.mbhk.barcode4j.ConfigurationException;

/**
 *
 * @author mk
 */
public class ConfigurationBuilder {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationBuilder.class.getName());

    private ConfigurationBuilder() {
        // utility class
    }

    public static Configuration createEmptyConfiguration() {
        return new Configuration("cfg");
    }

    public static Configuration createDefaultConfiguration(String symbologie) {
        return (symbologie == null || symbologie.isEmpty()) ? createEmptyConfiguration()
                : new Configuration(symbologie);
    }

    public static Configuration buildFromFile(String filename) throws BarcodeException {
        LOGGER.log(Level.INFO, "Using configurationfile: {}", filename);
        return buildFromFile(Paths.get(filename));
    }

    public static Configuration buildFromFile(Path inputFile) throws BarcodeException {
        if (!(Files.exists(inputFile) && Files.isRegularFile(inputFile) && Files.isReadable(inputFile))) {
            throw new BarcodeException("ConfigurationFile not readable: " + inputFile);
        }
        try {
            return Configuration.builder().buildFromFile(inputFile);
        } catch (ConfigurationException ex) {
            throw new BarcodeException("Error reading ConfigurationFile", ex);
        }
    }
}
