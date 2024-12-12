package com.mudcode.springboot.freemarker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mudcode.springboot.ApplicationTest;
import com.mudcode.springboot.common.util.JsonUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FreeMarkerServiceTest extends ApplicationTest {

    @Autowired
    private Configuration freeMarkerConfiguration;

    @Autowired
    private FreeMarkerService freeMarkerService;

    private Map<String, Object> map;

    @BeforeEach
    public void before() {
        map = new HashMap<>();
        map.put("name", "free marker");
    }

    @Test
    public void test0() throws IOException, TemplateException {
        Template template = freeMarkerConfiguration.getTemplate("hello.ftl");
        logger.info(FreeMarkerTemplateUtils.processTemplateIntoString(template, map));
    }

    @Test
    public void test1() throws IOException, TemplateException {
        logger.info(freeMarkerService.process("hello.ftl", map));
    }

    @Test
    public void test2() throws IOException, TemplateException, URISyntaxException {
        String body = new String(
                Files.readAllBytes(Paths.get(this.getClass().getResource("/json/alert.json").toURI())));
        Map<String, Object> map = JsonUtil.toObject(body, new TypeReference<Map<String, Object>>() {
        });
        logger.info(freeMarkerService.process("prom-alert.ftl", map));
    }

}
