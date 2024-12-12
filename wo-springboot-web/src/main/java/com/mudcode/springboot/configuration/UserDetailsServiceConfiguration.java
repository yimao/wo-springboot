package com.mudcode.springboot.configuration;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class UserDetailsServiceConfiguration {

    @Bean
    public UserDetailsService userDetailsService(SecurityProperties securityProperties) {
        UserDetails oneUser = User.withUsername(securityProperties.getUser().getName())
                .password(securityProperties.getUser().getPassword())
                .roles("LOGIN")
                .build();
        return new InMemoryUserDetailsManager(oneUser);
    }

}
