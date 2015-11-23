package com.github.mbhk.barcode4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Configuration {
    private final String name;
    private String value;
    private final Map<String, String> attributes = new HashMap<String, String>();
    private final Map<String, Configuration> childs = new HashMap<String, Configuration>();

    public Configuration(String name) {
        this.name = name;
    }

    public void addChild(Configuration cfg) {
        childs.put(cfg.getName(), cfg);
    }

    public String getAttribute(String string) throws ConfigurationException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAttribute(String string, String defaultValue) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean getAttributeAsBoolean(String string, boolean b) {
        // TODO Auto-generated method stub
        return false;
    }

    public int getAttributeAsInteger(String string, int i) {
        final String tmp = attributes.get(string);
        int res = i;
        try {
            res = Integer.parseInt(tmp);
        } catch (Exception ex) {
            // ignore
        }
        return res;
    }

    public Configuration getChild(String name) throws ConfigurationException {
        return getChild(name, true);
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

    public boolean getValueAsBoolean() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean getValueAsBoolean(boolean defaultDisplayStartStop) {
        // TODO Auto-generated method stub
        return false;
    }

    public double getValueAsFloat() {
        // TODO Auto-generated method stub
        return 0;
    }

    public double getValueAsFloat(double defaultValue) {
        // TODO Auto-generated method stub
        return 0;
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
        attributes.put(key, value);

    }

    public void setValue(String newValue) {
        value = newValue;
    }
}
