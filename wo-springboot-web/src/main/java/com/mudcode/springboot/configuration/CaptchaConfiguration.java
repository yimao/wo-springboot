package com.mudcode.springboot.configuration;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfiguration {

    @Bean("captchaProducer")
    public DefaultKaptcha getDefaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.put(Constants.KAPTCHA_IMAGE_WIDTH, "100");
        properties.put(Constants.KAPTCHA_IMAGE_HEIGHT, "50");
        properties.put(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
        properties.put(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, "1234567890");
        defaultKaptcha.setConfig(new Config(properties));
        return defaultKaptcha;
    }

}
