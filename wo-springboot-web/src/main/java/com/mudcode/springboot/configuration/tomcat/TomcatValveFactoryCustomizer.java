package com.mudcode.springboot.configuration.tomcat;

import org.apache.catalina.Valve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

public class TomcatValveFactoryCustomizer
        implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>, Ordered {

    private final Valve valve;

    public TomcatValveFactoryCustomizer(Valve valve) {
        this.valve = valve;
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        // factory.addContextValves(new RequestContextInfoValve(true));
        factory.addContextValves(valve);
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
