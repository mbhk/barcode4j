/*
 * Copyright 2006 Jeremias Maerki.
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

package org.krysalis.barcode4j.impl.datamatrix;

import java.awt.Dimension;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;

/**
 * Top-level class for the logic part of the DataMatrix implementation.
 *
 * @version 1.2
 */
public class DataMatrixLogicImpl {
    private static final Logger LOGGER = Logger.getLogger(DataMatrixLogicImpl.class.getName());

    /**
     * Generates the barcode logic.
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     * @param shape the symbol shape constraint
     * @param minSize the minimum symbol size constraint or null for no constraint
     * @param maxSize the maximum symbol size constraint or null for no constraint
     */
    public void generateBarcodeLogic(TwoDimBarcodeLogicHandler logic, String msg,
            SymbolShapeHint shape, Dimension minSize, Dimension maxSize) {

        //ECC 200
        //1. step: Data encodation
        String encoded;
        try {
            encoded = DataMatrixHighLevelEncoder.encodeHighLevel(msg, shape, minSize, maxSize);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot fetch data", e);
        }

        final DataMatrixSymbolInfo symbolInfo = DataMatrixSymbolInfo.lookup(encoded.length(),
                shape, minSize, maxSize, true);
        LOGGER.log(Level.FINE, symbolInfo.toString());

        //2. step: ECC generation
        final String codewords = DataMatrixErrorCorrection.encodeECC200(
                encoded, symbolInfo);

        //3. step: Module placement in Matrix
        final DefaultDataMatrixPlacement placement = new DefaultDataMatrixPlacement(
                    codewords,
                    symbolInfo.getSymbolDataWidth(), symbolInfo.getSymbolDataHeight());
        placement.place();

        //4. step: low-level encoding
        logic.startBarcode(msg, msg);
        encodeLowLevel(logic, placement, symbolInfo);
        logic.endBarcode();
    }

    private void encodeLowLevel(TwoDimBarcodeLogicHandler logic,
            DataMatrixPlacement placement, DataMatrixSymbolInfo symbolInfo) {
        final int symbolWidth = symbolInfo.getSymbolDataWidth();
        final int symbolHeight = symbolInfo.getSymbolDataHeight();
        for (int y = 0; y < symbolHeight; y++) {
            if ((y % symbolInfo.getMatrixHeight()) == 0) {
                logic.startRow();
                for (int x = 0; x < symbolInfo.getSymbolWidth(); x++) {
                    logic.addBar((x % 2) == 0, 1);
                }
                logic.endRow();
            }
            logic.startRow();
            for (int x = 0; x < symbolWidth; x++) {
                if ((x % symbolInfo.getMatrixWidth()) == 0) {
                    logic.addBar(true, 1); //left finder edge
                }
                logic.addBar(placement.getBit(x, y), 1);
                if ((x % symbolInfo.getMatrixWidth()) == symbolInfo.getMatrixWidth() - 1) {
                    logic.addBar((y % 2) == 0, 1); //right finder edge
                }
            }
            logic.endRow();
            if ((y % symbolInfo.getMatrixHeight()) == symbolInfo.getMatrixHeight() - 1) {
                logic.startRow();
                for (int x = 0; x < symbolInfo.getSymbolWidth(); x++) {
                    logic.addBar(true, 1);
                }
                logic.endRow();
            }
        }
    }
}
