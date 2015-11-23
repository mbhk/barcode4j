package com.github.mbhk.barcode4j;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.Test;

public class ConfigurationTest {

    @Test
    public void testGood() throws Exception {
        Configuration cfg = Configuration.builder().buildFromFile(
                Paths.get(Thread.currentThread().getContextClassLoader().getResource("xml/good-cfg.xml").toURI()));
        assertEquals("ean-13", cfg.getName());
    }
    
    @Test
    public void testBad() throws Exception {
        Configuration cfg = Configuration.builder().buildFromFile(
                Paths.get(Thread.currentThread().getContextClassLoader().getResource("xml/bad-cfg.xml").toURI()));
        assertEquals("ean-13", cfg.getName());
    }
}
