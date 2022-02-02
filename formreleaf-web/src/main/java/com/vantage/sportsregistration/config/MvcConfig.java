package com.vantage.sportsregistration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private MessageSource messageSource;

    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/accessDenied").setViewName("accessDenied");
    }

    @Bean
    public MessageDigestPasswordEncoder messageDigestPasswordEncoder() {

        return new MessageDigestPasswordEncoder("sha-256");
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor() {

        return new MessageSourceAccessor(messageSource);
    }
}
