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
package org.krysalis.barcode4j.impl.upcean;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class implements the EAN-8 barcode.
 * 
 * @author Jeremias Maerki
 * @version 1.1
 */
public class EAN8Bean extends UPCEANBean {

    @Override
    public UPCEANLogicImpl createLogicImpl() {
        return new EAN8LogicImpl(getChecksumMode());
    }
    
    @Override
    int getModuleCount() {
        return 3 /* left guard */ + 4*7 + 5 /* center guard */ + 4*7 + 3 /* right guard */;
    }

    @Override
    public Collection<String> getAdditionalNames() {
        final Collection<String> res = new ArrayList<String>(1);
        res.add("ean8");
        return res;
    }

    @Override
    public String getId() {
        return "ean-8";
    }
}
