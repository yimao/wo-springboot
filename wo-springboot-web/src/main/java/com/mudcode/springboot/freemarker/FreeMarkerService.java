package com.mudcode.springboot.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;

@Component
public class FreeMarkerService {

    private static final Logger logger = LoggerFactory.getLogger(FreeMarkerService.class);

    @Autowired
    private Configuration freeMarkerConfiguration;

    public String process(String templateName, Object object) throws IOException, TemplateException {
        Template template = freeMarkerConfiguration.getTemplate(templateName);
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, object);
        logger.trace(text);
        return text;
    }

}
