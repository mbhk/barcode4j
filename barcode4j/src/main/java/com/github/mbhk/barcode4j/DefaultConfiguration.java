package com.github.mbhk.barcode4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class DefaultConfiguration extends Configuration {

    public static class Builder {
        private static final Logger LOGGER = Logger.getLogger(Builder.class.getName());

        public Configuration buildFromFile(Path inputFile) throws ConfigurationException {
            // TODO Auto-generated method stub

            SAXBuilder sb = new SAXBuilder();
            Document doc = null;
            Configuration res = null;
            try {
                doc = sb.build(inputFile.toFile());
                Element rootElement = doc.getRootElement();
                res = processElement(rootElement, true);
            } catch (JDOMException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return res;
        }

        private Configuration processElement(Element elem, boolean isRoot) {
            if (isRoot && elem.getChildren().size() == 1) {
                LOGGER.log(Level.FINE, "skipping rootElement");
                return processElement(elem.getChildren().get(0), isRoot);
            }

            Configuration res = new Configuration(elem.getName());
            res.setValue(elem.getText());
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

    public DefaultConfiguration(String name) {
        super(name);
    }

}
