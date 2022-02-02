package com.vantage.sportsregistration.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.TimeZone;

import static java.lang.Runtime.getRuntime;

/**
 * @author Bazlur Rahman Rokon
 * @since 8/10/15.
 */
@Component
public class LoggingBean implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingBean.class);

    @Autowired
    private Environment environment;

    @Autowired
    private ServletContext servletContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("=============================================================");
        try {
            logEnvVariables();
            logSpringProfiles();
            logRelevantProperties();

        } catch (final Exception e) {
            LOGGER.error("LoggingBean Failed", e);
        }

        LOGGER.info("=============================================================");
    }

    private void logRelevantProperties() {

        final String databaseUrl = "spring.datasource.url";
        String dev = getValueOfProperty(environment, databaseUrl, null, null);
        LOGGER.info("{} = {}", databaseUrl, dev);

        final String thymeleafCacheKey = "spring.thymeleaf.cache";
        String thymeleafCacheValue = getValueOfProperty(environment, thymeleafCacheKey, null, null);
        LOGGER.info("{} = {}", thymeleafCacheKey, thymeleafCacheValue);

        final String ddlAutoKey = "spring.jpa.hibernate.ddl-auto";
        String ddlAutoValue = getValueOfProperty(environment, ddlAutoKey, null, null);
        LOGGER.info("{} = {}", ddlAutoKey, ddlAutoValue);

        final Runtime runtime = getRuntime();
        final long freeMemory = runtime.freeMemory();
        LOGGER.info("Server : {}", servletContext.getServerInfo());
        LOGGER.info("Free memory: {}", freeMemory);
        LOGGER.info("Total Memory: {}", runtime.totalMemory());
        LOGGER.info("Used Memory: {}", runtime.totalMemory() - freeMemory);
        LOGGER.info("Max Memory: {}", runtime.maxMemory());


        TimeZone timeZone = TimeZone.getDefault();
        LOGGER.info("TimeZone: {}", timeZone.getDisplayName());

    }

    private void logSpringProfiles() {
        //TODO
    }

    private void logEnvVariables() {
        //TODO
    }

    private String getValueOfProperty(final Environment environment, final String propertyKey, String propertyDefaultValue, final List<String> acceptablePropertyValue) {
        String propValue = environment.getProperty(propertyKey);

        if (propValue == null) {
            propValue = propertyDefaultValue;
            LOGGER.warn("The {} doesn't have an explicit value; default value is: {}", propertyKey, propertyDefaultValue);
        }

        if (acceptablePropertyValue != null) {
            if (!acceptablePropertyValue.contains(propValue)) {
                LOGGER.warn("The property = {} has an invalid value = {}", propertyKey, propValue);
            }
        }

        if (propValue == null) {
            LOGGER.warn("The property = {} is null", propertyKey);
        }

        return propValue;
    }
}
