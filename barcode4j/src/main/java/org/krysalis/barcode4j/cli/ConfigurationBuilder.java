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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.krysalis.barcode4j.BarcodeException;
import org.xml.sax.SAXException;

/**
 *
 * @author mk
 */
public class ConfigurationBuilder {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationBuilder.class.getName());

    private ConfigurationBuilder() {
        // utility class
    }

    public static DefaultConfiguration createEmptyConfiguration() {
        return new DefaultConfiguration("cfg");
    }

    public static Configuration createDefaultConfiguration(String symbologie) {
        final DefaultConfiguration cfg = createEmptyConfiguration();
        if (symbologie != null && !symbologie.isEmpty()) {
            final DefaultConfiguration child = new DefaultConfiguration(symbologie);
            cfg.addChild(child);
        }
        return cfg;
    }

    public static Configuration buildFromFile(String filename) throws BarcodeException {
        LOGGER.log(Level.INFO, "Using configurationfile: {}", filename);
        final File cfgFile = new File(filename);
        return buildFromFile(cfgFile);
    }

    public static Configuration buildFromFile(File inputFile) throws BarcodeException {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Throwable t = null;
        Configuration res = null;
        if (!(inputFile.exists() && inputFile.isFile() && inputFile.canRead())) {
            throw new BarcodeException("ConfigurationFile not readable: " + inputFile.getName());
        }
        try {
            res = builder.buildFromFile(inputFile);
        } catch (SAXException ex) {
            t = ex;
        } catch (IOException ex) {
            t = ex;
        } catch (ConfigurationException ex) {
            t = ex;
        }

        if (t != null) {
            throw new BarcodeException("Error reading ConfigurationFile", t);
        }
        return res;
    }
}
