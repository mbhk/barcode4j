package com.github.mbhk.barcode4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.util.Set;

public class Configuration {
    private final String name;
    private String value;
    private final Map<String, String> attributes = new HashMap<String, String>();
    private final Map<String, Configuration> childs = new HashMap<String, Configuration>();
// TODO make accessors thread-safe
    public Configuration(String name) {
        this(name, null);
    }

    public Configuration(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null.");
        }
        this.name = name.trim();
        this.value = value;
    }

    public void addChild(Configuration cfg) {
        childs.put(cfg.getName(), cfg);
    }

    public String getAttribute(String attributeName) throws ConfigurationException {
        if (!attributes.containsKey(attributeName)) {
            throw new ConfigurationException("No attribute with name " + attributeName);
        }
        return attributes.get(attributeName);
    }

    public String getAttribute(String attributeName, String defaultValue) {
        if (!attributes.containsKey(attributeName)) {
            return defaultValue;
        }
        return attributes.get(attributeName);
    }

    public boolean getAttributeAsBoolean(String attributeName, boolean defaultValue) {
        return attributes.containsKey(attributeName) ? Boolean.parseBoolean(attributes.get(attributeName))
                : defaultValue;
    }

    public int getAttributeAsInteger(String attributeName, int i) {
        final String tmp = attributes.get(attributeName);
        int res = i;
        try {
            res = Integer.parseInt(tmp);
        } catch (Exception ex) {
            // ignore
        }
        return res;
    }

    public Configuration getChild(String name) throws ConfigurationException {
        return childs.containsKey(name) ? getChild(name, true) : new Configuration(name);
    }

    public Configuration getChild(String name, boolean failOnMissing) throws ConfigurationException {

        if (failOnMissing && !childs.containsKey(name)) {
            throw new ConfigurationException("no configuration " + name + " registered!");
        } else {
            return childs.get(name);
        }
    }

    public Configuration[] getChildren() {
        final Set<Entry<String, Configuration>> entrySet = childs.entrySet();
        final Configuration[] res = new Configuration[entrySet.size()];
        int i = 0;
        for (Entry<String, Configuration> child : entrySet) {
            res[i++] = child.getValue();
        }
        return res;
    }

    public String getName() {
        return name;
    }

    public String getValue() throws ConfigurationException {
        if (value == null) {
            throw new ConfigurationException("No value.");
        }
        return value;
    }

    public String getValue(String defaultValue) {
        return value == null ? defaultValue : value;
    }

    public boolean getValueAsBoolean() throws ConfigurationException {
        final String tmp = getValue();
        return Boolean.parseBoolean(tmp);
    }

    public boolean getValueAsBoolean(boolean defaultValue) {
        boolean res;
        try {
            res = getValueAsBoolean();
        } catch (ConfigurationException e) {
            res = defaultValue;
        }
        return res;
    }

    public double getValueAsFloat() {
        return Double.parseDouble(value);
    }

    public double getValueAsFloat(double defaultValue) {
        return value == null ? defaultValue : Double.parseDouble(value);
    }

    public int getValueAsInteger() throws ConfigurationException {
        int res;
        try {
            res = Integer.parseInt(getValue());
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage());
        }
        return res;
    }

    public int getValueAsInteger(int defaultValue) {
        int res = defaultValue;
        try {
            res = getValueAsInteger();
        } catch (Exception e) { // ignore
        }

        return res;
    }

    public void setAttribute(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null.");
        }
        attributes.put(key.trim(), trimIfNonNull(value));
    }

    public void setValue(String value) {
        this.value = trimIfNonNull(value);
    }

    private String trimIfNonNull(String in) {
        return in == null ? null : in.trim();
    }

    public static class Builder {
        private static final Logger LOGGER = Logger.getLogger(Builder.class.getName());

        private Builder() {
            // hide constructor
        }

        public Configuration buildFromFile(Path inputFile) throws ConfigurationException {

            SAXBuilder sb = new SAXBuilder();
            Document doc = null;
            Configuration res = null;
            try {
                doc = sb.build(inputFile.toFile());
                Element rootElement = doc.getRootElement();
                res = processElement(rootElement, true);
            } catch (JDOMException e) {
                throw new ConfigurationException("Configurationfile is not parseable.");
            } catch (IOException e) {
                throw new ConfigurationException("Failure while reading configurationfile.");
            }
            return res;
        }

        private Configuration processElement(Element elem, boolean isRoot) {
            if (isRoot && elem.getChildren().size() == 1) {
                LOGGER.log(Level.FINE, "skipping rootElement");
                return processElement(elem.getChildren().get(0), false);
            }

            Configuration res = new Configuration(elem.getName(), elem.getText());
            for (Attribute attribute : elem.getAttributes()) {
                res.setAttribute(attribute.getName(), attribute.getValue());
            }
            for (Element curr : elem.getChildren()) {
                res.addChild(processElement(curr, false));
            }
            return res;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
