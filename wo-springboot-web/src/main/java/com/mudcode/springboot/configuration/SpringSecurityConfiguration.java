package com.mudcode.springboot.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

    @Value("${spring.security.ignored-url:}")
    private List<String> ignoredUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ValidateLoginCaptchaCodeFilter validateLoginCaptchaCodeFilter) throws Exception {

        Set<String> staticResources = Arrays.stream(StaticResourceLocation.values())
                .flatMap(StaticResourceLocation::getPatterns)
                .collect(Collectors.toSet());
        if (ignoredUrl != null) {
            staticResources.addAll(ignoredUrl);
        }
        String[] ignoredUrls = staticResources.toArray(new String[0]);

        http.authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                                .requestMatchers(ignoredUrls)
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .httpBasic(httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer.realmName("Closed Area"))
                .headers(headersConfigurer -> headersConfigurer.cacheControl(cacheControlConfig -> {
                        }).contentTypeOptions(contentTypeOptionsConfig -> {
                        })
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .xssProtection(xXssConfig -> xXssConfig
                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)))
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.ignoringRequestMatchers("/**"));

        http.addFilterBefore(validateLoginCaptchaCodeFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
