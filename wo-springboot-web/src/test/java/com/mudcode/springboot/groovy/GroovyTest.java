package com.mudcode.springboot.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GroovyTest {

    private static final Logger logger = LoggerFactory.getLogger(GroovyTest.class);

    @Test
    public void test() throws URISyntaxException, IOException {
        URL file = this.getClass().getResource("/groovy/script.groovy");
        String script = Files.readString(Paths.get(file.toURI()));

        Binding binding = new Binding();
        binding.setVariable("name", "yimao");

        GroovyShell shell = new GroovyShell(binding);
        Object result = shell.evaluate(script);

        logger.info("Script result: {}", result);
    }

}
