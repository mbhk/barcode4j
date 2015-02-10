/*
 * Copyright 2002-2004,2008 Jeremias Maerki.
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
package org.krysalis.barcode4j.tools;

import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a length (value plus unit).
 *
 * It is used to parse expressions like "0.21mm".
 * 
 * @author Jeremias Maerki
 * @author mk
 * @version 1.2
 */
public class Length {
    private static final Logger LOGGER = Logger.getLogger(Length.class.getName());
    
    /** String constant for inches. */
    public static final String INCH = "in";
    /** String constant for points. */
    public static final String POINT = "pt";
    /** String constant for centimeters. */
    public static final String CM = "cm";
    /** String constant for millimeters. */
    public static final String MM = "mm";
    
    private double value;
    private String unit;
    
    /**
     * Creates a Length instance.
     * @param value the value
     * @param unit the unit (ex. "cm")
     */
    public Length(double value, String unit) {
        this.value = value;
        this.unit = unit.toLowerCase();
    }
    
    /**
     * Creates a Length instance.
     * @param text the String to parse
     * @param defaultUnit the default unit to assume
     */
    public Length(String text, String defaultUnit) {
        final Builder b = new Builder();
        try {
            b.fromString(text);
        } catch (Exception e) {
            LOGGER.log(Level.FINEST, "Length not buildable fromString.", e);
            b.reset().withValue(text).withUnit(defaultUnit);
        }
        final Length l = b.build();
        this.unit = l.unit;
        this.value = l.value;
    }
    
    /**
     * Creates a Length instance. The default unit assumed is "mm".
     * @param text the String to parse
     */
    public Length(String text) {
        this(text, null);
    }

    /**
     * Returns the unit.
     * @return String
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * Returns the value.
     * @return double
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Returns the value converted to internal units (mm).
     * @return the value (in mm)
     */
    public double getValueAsMillimeter() {
        if (this.unit.equals(MM)) {
            return this.value;
        } else if (this.unit.equals(CM)) {
            return this.value * 10;
        } else if (this.unit.equals(POINT)) {
            return UnitConv.pt2mm(this.value);
        } else if (this.unit.equals(INCH)) {
            return UnitConv.in2mm(this.value);
        } else {
            throw new IllegalStateException("Don't know how to convert " 
                    + this.unit + " to mm");
        }
    }
    
    /**
     * Gets als supported unit types.
     *
     * @return List of supported unit
     */
    public static Set<String> getSupportedUnits() {
        final Set<String> res = new TreeSet<String>();
        res.add(CM);
        res.add(INCH);
        res.add(POINT);
        res.add(MM);
        return res;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Length other = (Length) obj;
        if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        return this.unit == null ? other.unit == null : this.unit.equals(other.unit);
    }

    @Override
    public String toString() {
        return getValue() + getUnit();
    }
    
    public static class Builder {
        private Double value = null;
        private String unit = null;
        private final Set<String> supportedUnits;
        private static final Pattern UNIT_PATTERN = Pattern.compile("[\\s]*([a-zA-Z]+)[\\s]*.*$");
        private static final Pattern VALUE_PATTERN = Pattern.compile("^(([\\d]?[\\.,][\\d]+)|([\\d]+))[\\s]*");

        public Builder() {
            supportedUnits = Length.getSupportedUnits();
        }

        public Builder withValue(double value) {
            this.value = value;
            return this;
        }

        public Builder withValue(String value) {
            this.value = parseValue(value);
            return this;
        }

        public Builder withUnit(String unit) {
            this.unit = checkUnit(unit);
            return this;
        }

        public Builder fromString(String valueWithUnit) {
            if (valueWithUnit == null) {
                throw new IllegalArgumentException("argument must not be null");
            }

            final String tmp = valueWithUnit.trim();
            final String tmpUnit = checkUnit(extractUnit(tmp));
            this.value = parseValue(extractValue(tmp));
            this.unit = tmpUnit;
            return this;
        }

        public Length build() {
            if (value == null || unit == null) {
                throw new IllegalStateException("you have to specify value and unit " + value + unit);
            }
            return new Length(value, unit);
        }

        public Builder reset() {
            this.unit = null;
            this.value = null;
            return this;
        }

        private double parseValue(String value) {
            return Double.parseDouble(value.replaceAll(",", "."));
        }

        private String extractUnit(String valueWithUnit) {
            final Matcher m = UNIT_PATTERN.matcher(valueWithUnit);
            if (m.find()) {
                return m.group(1);
            } else {
                throw new IllegalArgumentException("unit not found in " + valueWithUnit);
            }
        }

        private String extractValue(String valueWithUnit) {
            final Matcher m = VALUE_PATTERN.matcher(valueWithUnit);
            if (m.find()) {
                return m.group(1);
            } else {
                throw new IllegalArgumentException("value not found in " + valueWithUnit);
            }
        }

        private String checkUnit(String unit) {
            if(unit == null) {
                throw new IllegalArgumentException("unit must not be null");
            }
            final String tmp = unit.trim().toLowerCase();
            if (!supportedUnits.contains(tmp)) {
                throw new IllegalArgumentException("Unsupported unit " + tmp);
            }
            return tmp;
        }
    }
}
