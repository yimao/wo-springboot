package com.mudcode.springboot.configuration;

import com.mudcode.springboot.filter.MDCRequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean<MDCRequestFilter> mdcRequestFilter() {
        MDCRequestFilter mdcRequestFilter = new MDCRequestFilter();
        return new FilterRegistrationBean<>(mdcRequestFilter);
    }

}
