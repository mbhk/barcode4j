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
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.krysalis.barcode4j.BarcodeException;
import org.xml.sax.SAXException;

/**
 *
 * @author mk
 */
public class ConfigurationBuilder {

    private ConfigurationBuilder() {
        // utility class
    }

    public static Configuration buildFromFile(String inputFilename) throws BarcodeException {
        final File cfgFile = new File(inputFilename);
        if (!(cfgFile.exists() && cfgFile.isFile() && cfgFile.canRead())) {
            throw new BarcodeException("ConfigurationFile not readable: " + inputFilename);
        }
        return buildFromFile(cfgFile);
    }

    public static Configuration buildFromFile(File inputFile) throws BarcodeException {
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Throwable t = null;
        Configuration res = null;
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
