package com.mudcode.springboot.configuration.tomcat;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(DispatcherServlet.class)
@ConditionalOnProperty(name = "tomcat.request.monitor", havingValue = "true", matchIfMissing = false)
public class TomcatMonitor {

    @Bean
    @ConditionalOnClass(name = "org.apache.catalina.startup.Tomcat")
    public TomcatValveFactoryCustomizer tomcatValveFactoryCustomizer() {
        return new TomcatValveFactoryCustomizer(new RequestContextInfoValve(true));
    }

}
