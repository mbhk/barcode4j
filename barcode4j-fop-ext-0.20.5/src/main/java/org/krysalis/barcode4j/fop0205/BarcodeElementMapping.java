/*
 * Copyright 2003-2004,2007 Jeremias Maerki or contributors to Barcode4J, as applicable
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
package org.krysalis.barcode4j.fop0205;

import java.util.Collection;
import java.util.HashMap;

import org.apache.fop.fo.DirectPropertyListBuilder;
import org.apache.fop.fo.ElementMapping;
import org.apache.fop.fo.TreeBuilder;
import org.apache.fop.fo.FObj;

import org.krysalis.barcode4j.BarcodeConstants;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;

/**
 * Registers the elements covered by Barcode4J's namespace.
 * 
 * @version $Id$
 */
public class BarcodeElementMapping implements ElementMapping {

    private static HashMap foObjs = null;    
    
    protected FObj.Maker getBarcodeElementMaker() {
        return BarcodeElement.maker();
    }
    
    protected FObj.Maker getBarcodeObjMaker(String name) {
        return BarcodeObj.maker(name);
    }
    
    private synchronized void setupBarcodeElements() {
        if (foObjs == null) {
            foObjs = new HashMap();
            final Collection<String> elements = ConfigurableBarcodeGenerator.getBarcodeElements();
            foObjs.put("barcode", getBarcodeElementMaker());
            for (final String element : elements) {
                foObjs.put(element, getBarcodeObjMaker(element));
            }
        }
    }

    @Override
    public void addToBuilder(TreeBuilder builder) {
        setupBarcodeElements();
        builder.addMapping(BarcodeConstants.NAMESPACE.toString(), foObjs);

        builder.addPropertyListBuilder(BarcodeConstants.NAMESPACE.toString(), new DirectPropertyListBuilder());
        
        //for compatibility (Krysalis Barcode)
        builder.addMapping(BarcodeConstants.OLD_NAMESPACE.toString(), foObjs);
        builder.addPropertyListBuilder(BarcodeConstants.OLD_NAMESPACE.toString(), 
                new DirectPropertyListBuilder());
    }
}

