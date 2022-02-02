package com.formreleaf.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;

import javax.sql.DataSource;

/**
 * @author Bazlur Rahman Rokon
 * @since 6/18/15.
 */
@Configuration
public class DatabaseConfiguration implements EnvironmentAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private Environment env;
    private RelaxedPropertyResolver dataSourcePropertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
        this.dataSourcePropertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
    }

    @Bean(destroyMethod = "shutdown")
    public DataSource dataSource() {
        LOGGER.info("Configuring Datasource");

        if (dataSourcePropertyResolver.getProperty("url") == null) {
            LOGGER.error("Your database connection pool configuration is incorrect! The application cannot start.");
            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }

        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(dataSourcePropertyResolver.getProperty("dataSourceClassName"));
        config.addDataSourceProperty("url", dataSourcePropertyResolver.getProperty("url"));
        config.addDataSourceProperty("user", dataSourcePropertyResolver.getProperty("username"));
        config.addDataSourceProperty("password", dataSourcePropertyResolver.getProperty("password"));

        return new HikariDataSource(config);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
