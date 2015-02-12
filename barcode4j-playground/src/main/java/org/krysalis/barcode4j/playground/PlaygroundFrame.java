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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.krysalis.barcode4j.BarcodeGeneratorProvider;

/**
 * A Frame showing an barcode plus some controls to configure the barcode.
 *
 * @version 2.1.2
 */
public class PlaygroundFrame extends JFrame implements ActionListener, ChangeListener {

    public static final String TITLE = "Barcode Playground";
    private static final long serialVersionUID = 3241340455560147586L;

    private BarcodePanel bcpanel = null;
    private JComboBox<String> barcodeNames = null;
    private JTextField messageField = null;
    private JSlider orientation = null;
    private JPanel parametersPanel;

    private BarcodePanel getBarcodePanel() {
        if (bcpanel == null) {
            bcpanel = new BarcodePanel();
        }
        return bcpanel;
    }

    private JComboBox<String> getBarcodeNames() {
        if (barcodeNames == null) {
            barcodeNames = new JComboBox<String>();
            final Collection<String> names = BarcodeGeneratorProvider.getInstance()
                    .getAvailableBarcodeGenerators();
            for (final String name : names) {
                barcodeNames.addItem(name);
            }
            barcodeNames.setSelectedItem("qr");
            barcodeNames.addActionListener(this);
        }
        return barcodeNames;
    }

    private JTextField getMessageField() {
        if (messageField == null) {
            messageField = new JTextField("0123456");
            messageField.getDocument().addDocumentListener(new DocumentListenerImpl());
        }
        return messageField;
    }

    private JSlider getOrientation() {
        if (orientation == null) {
            orientation = new JSlider(0, 270, 0);
            orientation.setMinorTickSpacing(90);
            orientation.setPaintTicks(true);
            orientation.setSnapToTicks(true);
            orientation.addChangeListener(this);
        }
        return orientation;
    }

    private void buildGUI() {
        setTitle(TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));

        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                getParametersPanel(), getBarcodePanel());
        setContentPane(splitPane);

        getBarcodePanel().setBarcodeName((String) getBarcodeNames().getSelectedItem());
        getBarcodePanel().setMessage(getMessageField().getText());
        getBarcodePanel().setOrientation(getOrientation().getValue());
    }

    /**
     * Constructs the frame and brings it upfront.
     */
    public void createAndShowGUI() {
        buildGUI();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getBarcodeNames().equals(e.getSource())) {
            getBarcodePanel().setBarcodeName((String) getBarcodeNames().getSelectedItem());
        }
    }

    private Component getParametersPanel() {
        if (parametersPanel == null) {
            parametersPanel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            parametersPanel.add(new JLabel("Choose Barcode: "), c);
            c.gridx = 1;
            c.gridy = 0;
            c.weightx = 0.5;
            c.fill = GridBagConstraints.HORIZONTAL;
            parametersPanel.add(getBarcodeNames(), c);
            c.gridx = 0;
            c.gridy = 1;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            parametersPanel.add(new JLabel("Message: "), c);
            c.gridx = 1;
            c.gridy = 1;
            c.weightx = 0.5;
            c.fill = GridBagConstraints.HORIZONTAL;
            parametersPanel.add(getMessageField(), c);
            c.gridx = 0;
            c.gridy = 2;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            parametersPanel.add(new JLabel("Orientation: "), c);
            c.gridx = 1;
            c.gridy = 2;
            c.weightx = 0.5;
            c.fill = GridBagConstraints.HORIZONTAL;
            parametersPanel.add(getOrientation(), c);
        }
        return parametersPanel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (getOrientation().equals(e.getSource())) {
            getBarcodePanel().setOrientation(getOrientation().getValue());
        }
    }

    /**
     * Helper class to handle events of the message input field.
     */
    private class DocumentListenerImpl implements DocumentListener {

        public DocumentListenerImpl() {
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            update();
        }

        private void update() {
            getBarcodePanel().setMessage(getMessageField().getText());
        }
    }
}
