package com.vantage.sportsregistration.config;

import com.vantage.sportsregistration.StartupHouseKeeper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Bazlur Rahman Rokon
 * @since 8/24/15.
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public StartupHouseKeeper startupHouseKeeper() {

        return new StartupHouseKeeper();
    }
}
