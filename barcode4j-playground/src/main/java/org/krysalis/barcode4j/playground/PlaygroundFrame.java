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
package org.krysalis.barcode4j.playground;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;

/**
 * @version $Id$
 */
public class PlaygroundFrame extends JFrame {

    public static final String TITLE = "Barcode Playground";
    private static final long serialVersionUID = 3241340455560147586L;
    private static final Logger LOGGER = Logger.getLogger(PlaygroundFrame.class.getName());

    private void buildGUI() {
        setTitle(TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 400);
        BarcodePanel bcpanel = new BarcodePanel();
        add("Center", bcpanel);

        try {
            DefaultConfiguration cfg = new DefaultConfiguration("code128");
            DefaultConfiguration child = new DefaultConfiguration("human-readable-font");
            //child.setValue("OCR-B 10 Pitch BT");
            child.setValue("Tahoma");
            cfg.addChild(child);

            BarcodeGenerator gen
                    = BarcodeUtil.getInstance().createBarcodeGenerator(cfg);

            bcpanel.setBarcode(gen);
            bcpanel.setMessage("Hello World!");
        } catch (ConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Configuration Exception", e);
        } catch (BarcodeException e) {
            LOGGER.log(Level.SEVERE, "Barcode Exception", e);
        }
    }

    public void createAndShowGUI() {
        buildGUI();
        setVisible(true);
    }
}
