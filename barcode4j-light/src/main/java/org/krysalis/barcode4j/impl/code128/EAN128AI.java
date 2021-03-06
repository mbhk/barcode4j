/*
 * Copyright 2005 Dietmar B�rkle.
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
package org.krysalis.barcode4j.impl.code128;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class keeps Informations about EAN 128 Application Identifiers (AIs).
 *
 * @author Dietmar B�rkle
 */
public class EAN128AI {

    private static final Logger LOGGER = Logger.getLogger(EAN128AI.class.getName());
    public static final byte MAX_LEN = 48; // Max according to EAN128 specification.
    public static final byte TYPE_ALPHA_NUM = 0;
    public static final byte TYPE_NUM = 1;
    public static final byte TYPE_ALPHA = 2;    //Unused at the moment, but mentioned by
    //the EAN128 specification.
    public static final byte TYPE_NUM_DATE = 3;
    public static final byte TYPE_ERROR = 4;
    /**
     * Check digit
     */
    public static final byte TYPE_CD = 5;
    private static final String[] TYPE_TO_STRING = {"an", "n", "a", "d", "e", "cd"};

    String id;
    byte lenID, lenMinAll, lenMaxAll;
    byte minLenAfterVariableLen;

    byte[] lenMin, lenMax, type, checkDigitStart;
    boolean fixed = false, canDoChecksumADD = false;

    private static final String[] FIXED_LEN_TABLE = new String[]{
        "00", "01", "02", "03", "04",
        "11", "12", "13", "14", "15", "16", "17", "18", "19",
        "20",
        "31", "32", "33", "34", "35", "36",
        "41"};
    private static final byte[] FIXED_LEN_VALUE_TABLE = new byte[]{
        20, 16, 16, 16, 18,
        8, 8, 8, 8, 8, 8, 8, 8, 8,
        4,
        10, 10, 10, 10, 10, 10,
        16};
    private static final EAN128AI dft = parseSpecPrivate("xx", "an1-48");
    private static Object[] aiTable = new Object[]{dft, dft, dft, dft, dft, dft, dft, dft, dft, dft};
    private static boolean propertiesLoaded = false;
    
    private EAN128AI(String id, byte lenID, byte[] type, byte[] lenMin, byte[] lenMax, byte[] checkDigitStart) {
        this.id = id;
        this.lenID = lenID;
        this.type = type;
        this.lenMin = lenMin;
        this.lenMax = lenMax;
        this.checkDigitStart = checkDigitStart;
        lenMinAll = lenMaxAll = minLenAfterVariableLen = 0;
        int idxVarLen = type.length;
        int idxFirstChecksum = -1;
        for (int i = 0; i < type.length; i++) {
            lenMinAll += lenMin[i];
            lenMaxAll += lenMax[i];
            if (i > idxVarLen) {
                minLenAfterVariableLen += lenMin[i];
            }
            if (lenMin[i] != lenMax[i]) {
                if (idxVarLen < type.length) {
                    throw new IllegalArgumentException("Only one Part with var len!"); //TODO
                }
                idxVarLen = i;
            }
            if (idxFirstChecksum == -1 && type[i] == TYPE_CD) {
                idxFirstChecksum = i;
            }
        }
        canDoChecksumADD = (idxFirstChecksum == type.length - 1 && lenMinAll == lenMaxAll);
    }

    private EAN128AI(String id, byte lenID, byte type, byte len) {
        this(id, lenID,
                new byte[]{type}, new byte[]{len}, new byte[]{len},
                new byte[]{CheckDigit.CD_NONE});
        fixed = true;
    }

    private static class AIProperties extends Properties {

        private static final long serialVersionUID = -3183436317318245881L;

        @Override
        public synchronized Object put(Object arg0, Object arg1) {
            final EAN128AI ai = parseSpecPrivate((String) arg0, (String) arg1);
            try {
                setAI((String) arg0, ai);
            } catch (Exception e) {
                System.err.println(e);
            }
            return super.put(arg0, arg1);
        }
    }

    private static void initFixedLen(String aiName, byte aiLen) {
        final byte lenID = (byte) aiName.length();
        final EAN128AI ai = new EAN128AI(aiName, lenID, TYPE_ALPHA_NUM, aiLen);
        try {
            setAI(aiName, ai);
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Initilization error.", e);
        }
    }

    static {
        for (int i = 0; i <= 9; i++) {
            initFixedLen("23" + i, (byte) (1 + 9 * i));
        }
        for (int i = FIXED_LEN_VALUE_TABLE.length - 1; i >= 0; i--) {
            initFixedLen(FIXED_LEN_TABLE[i], (byte) (FIXED_LEN_VALUE_TABLE[i] - 2));
        }
    }

    public static synchronized void loadProperties() {
        if (propertiesLoaded) {
            return;
        }

        final String bundlename = "EAN128AIs";
        final String filename = bundlename + ".properties";
        final Properties p = new AIProperties();
        try {
            InputStream is = EAN128AI.class.getResourceAsStream(filename);
            if (is == null) {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            }
            if (is == null) {
                final String rbName = EAN128AI.class.getPackage().getName() + "." + bundlename;
                final ResourceBundle rb = ResourceBundle.getBundle(rbName);
                final Enumeration<String> keys = rb.getKeys();
                while (keys.hasMoreElements()) {
                    final String key = (String) keys.nextElement();
                    p.put(key, rb.getObject(key));
                }
            } else {
                try {
                    p.load(is);
                } finally {
                    is.close();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, filename + " could not be loaded!", e);
            // Not loading EAN128AIs.properties is a severe error. 
            // But the code is still usable, if you use templates or do not rely on checkdigits.
            // Maybe it would be better to throw this exception and find out how this cold happen.
        }
        propertiesLoaded = true;
    }

    private static void checkFixed(EAN128AI aiNew, EAN128AI aiOld) {
        if (aiOld.fixed && !aiNew.fixed) {
            if (aiNew.lenMaxAll != aiNew.lenMinAll
                    || aiNew.lenID + aiNew.lenMinAll != aiOld.lenID + aiOld.lenMinAll) {
                throw new IllegalArgumentException("AI \"" + aiNew.toString()
                        + "\" must have fixed len: " + aiOld.lenID + "+" + aiOld.lenMinAll);
            }
            aiNew.fixed = true;
        }
    }

    private static void setAIHere(EAN128AI ai, Object[] aitParent) {
        for (int idx = 0; idx <= 9; idx++) {
            setAIHere(ai, aitParent, idx);
        }
    }

    private static void setAIHere(EAN128AI aiNew, Object[] aitParent, int idx) {
        final Object tmp = aitParent[idx];
        if (tmp instanceof EAN128AI) {
            final EAN128AI aiOld = (EAN128AI) tmp;
            if (aiNew.type[0] == TYPE_ERROR) {
                aiOld.type[0] = TYPE_ERROR;
            } else {
                checkFixed(aiNew, aiOld);
                aitParent[idx] = aiNew;
            }
        } else {
            setAIHere(aiNew, (Object[]) tmp);
        }
    }

    private static void setAI(String aiName, EAN128AI ai) {
        Object[] aitParent = aiTable;
        int aiLastRelevantIdx = aiName.length() - 1;
        while (aiLastRelevantIdx >= 0
                && !Character.isDigit(aiName.charAt(aiLastRelevantIdx))) {
            aiLastRelevantIdx--;
        }
        Object tmp;
        for (int i = 0; i <= aiLastRelevantIdx; i++) {
            final int idx = aiName.charAt(i) - '0';
            if (i == aiLastRelevantIdx) {
                setAIHere(ai, aitParent, idx);
            } else {
                tmp = aitParent[idx];
                if (tmp instanceof EAN128AI) {
                    tmp = new Object[]{tmp, tmp, tmp, tmp, tmp, tmp, tmp, tmp, tmp, tmp};
                    aitParent[idx] = tmp;
                }
                aitParent = (Object[]) tmp;
            }
        }
    }

    public static EAN128AI parseSpec(String ai, String spec) {
        final EAN128AI ret = parseSpecPrivate(ai, spec);
        checkAI(ret);
        return ret;
    }

    private static void parseSpecPrivate(int i, String spec,
            byte[] type, byte[] lenMin, byte[] lenMax, byte[] checkDigitStart) {
        int startLen = 0;
        checkDigitStart[i] = 1;
        lenMin[i] = lenMax[i] = -1;
        if (spec.startsWith("an")) {
            type[i] = TYPE_ALPHA_NUM;
            startLen = 2;
        } else if (spec.startsWith("a")) {
            type[i] = TYPE_ALPHA;
            startLen = 1;
        } else if (spec.startsWith("cd")) {
            type[i] = TYPE_CD;
            if (spec.length() > 2) {
                checkDigitStart[i] = Byte.parseByte(spec.substring(2));
            }
            lenMin[i] = lenMax[i] = 1;
            return;
        } else if (spec.startsWith("n")) {
            type[i] = TYPE_NUM;
            startLen = 1;
        } else if (spec.startsWith("d")) {
            type[i] = TYPE_NUM_DATE;
            lenMin[i] = lenMax[i] = 6;
            startLen = 1;
        } else if (spec.startsWith("e")) {
            type[i] = TYPE_ERROR;
            lenMin[i] = lenMax[i] = 0;
            return;
        } else {
            throw new IllegalArgumentException("Unknown type!");
        }

        final int hyphenIdx = spec.indexOf('-', startLen);
        if (hyphenIdx < 0) {
            lenMin[i] = lenMax[i] = parseByte(spec.substring(startLen), lenMin[i]);
        } else if (hyphenIdx == startLen) {
            lenMin[i] = 1;
            lenMax[i] = parseByte(spec.substring(startLen + 1), lenMax[i]);
        } else {
            // hyphenIdx > startLen
            lenMin[i] = parseByte(spec.substring(startLen, hyphenIdx), lenMin[i]);
            lenMax[i] = parseByte(spec.substring(hyphenIdx + 1), lenMax[i]);
        }

        if (type[i] == TYPE_NUM_DATE && (lenMin[i] != 6 || lenMax[i] != 6)) {
            throw new IllegalArgumentException("Date field (" + spec + ") must have length 6!");
        }
    }

    private static byte parseByte(String val, byte defaultValue) {
        byte res= defaultValue;
        try {
            res = Byte.parseByte(val);
        } catch (NumberFormatException e) {
            if (defaultValue == -1) {
                throw new IllegalArgumentException("Can't read field length from \"" + val + "\"", e);
            }
        }
        return res;
    }

    private static EAN128AI parseSpecPrivate(String ai, String specIn) {
        try {
            final byte lenID = (byte) ai.trim().length();
            final String spec = specIn.trim();
            final StringTokenizer st = new StringTokenizer(spec, "+", false);
            final int count = st.countTokens();
            final byte[] type = new byte[count];
            final byte[] checkDigitStart = new byte[count];
            final byte[] lenMin = new byte[count];
            final byte[] lenMax = new byte[count];
            for (int i = 0; i < count; i++) {
                parseSpecPrivate(i, st.nextToken(), type, lenMin, lenMax, checkDigitStart);
            }
            return new EAN128AI(ai, lenID, type, lenMin, lenMax, checkDigitStart);
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Cannot Parse AI: \"" + ai + "\" spec: \"" + specIn + "\" ", e);
        }
    }

    public static boolean checkAI(EAN128AI ai) {
        final EAN128AI aiCompare = getAIPrivate(ai.id + "0000", 0);
        checkFixed(ai, aiCompare);
        return true;
    }

    public static EAN128AI getAI(String msg, int msgStart) {
        loadProperties();
        return getAIPrivate(msg, msgStart);
    }

    private static EAN128AI getAIPrivate(String msg, int msgStart) {
        EAN128AI ret = dft;
        Object o = aiTable;
        int c;
        for (int i = 0; i < msg.length() - msgStart; i++) {
            c = getIDChar(msg, msgStart + i) - '0';
            o = ((Object[]) o)[c];
            if (o == null) {
                return dft;
            }
            if (o instanceof EAN128AI) {
                ret = (EAN128AI) o;
                break;
            }
        }
        return ret;
    }

    private static char getIDChar(String msg, int msgStart) {
        char ret;
        try {
            ret = msg.charAt(msgStart);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to read AI: Message too short!", e);
        }
        if (!Character.isDigit(ret)) {
            throw new IllegalArgumentException("Unable to read AI: Characters must be numerical!");
        }
        return ret;
    }

    public static final boolean isCheckDigitType(byte type) {
        return type == TYPE_CD;
    }

    public final boolean isCheckDigit(byte idx) {
        return isCheckDigitType(type[idx]);
    }

    public static final String getType(byte type) {
        String ret = "?";
        try {
            ret = TYPE_TO_STRING[type];
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Unknown type " + type, e);
        }
        return ret;
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append('(').append(id).append(")");
        for (int i = 0; i < lenMin.length; i++) {
            if (i != 0) {
                ret.append('+');
            }
            ret.append(getType(type[i]));
            if (type[i] < TYPE_ERROR) {
                ret.append(lenMin[i]);
                if (lenMin[i] != lenMax[i]) {
                    ret.append('-').append(lenMax[i]);
                }
            }
        }
        ret.append((fixed) ? " (fixed)" : "");
        return ret.toString();
    }
}
