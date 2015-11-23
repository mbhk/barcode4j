package com.github.mbhk.barcode4j;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Test;

public class DefaultConfigurationTest {

    @Test
    public void test() throws ConfigurationException, URISyntaxException {
        Configuration cfg = DefaultConfiguration.builder().buildFromFile(
                Paths.get(Thread.currentThread().getContextClassLoader().getResource("xml/good-cfg.xml").toURI()));
        assertEquals("ean-13", cfg.getName());
    }
}
