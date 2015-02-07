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

import javax.swing.SwingUtilities;

/**
 * The Main class implements an application with a GUI to experiment
 * with the barcode4j library.
 * 
 * @version 2.1.2
 */
public class Main {

    /**
     * private contsructor to prevent instanciation.
     */
    private Main() {
    }

    /**
     * Initialize and show the GUI.
     *
     * @param args are ignored
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PlaygroundFrame mf = new PlaygroundFrame();
                mf.createAndShowGUI();
            }
        });
    }
}
