package com.formreleaf.scheduler;

import com.formreleaf.common.forms.FormProcessor;
import com.formreleaf.common.utils.Constants;
import com.formreleaf.config.FormReleafRepositoryFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;

@ComponentScan(basePackages = {"com.formreleaf.report.service", "com.formreleaf.scheduler"})
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
@EntityScan(basePackages = {"com.formreleaf.domain"})
@EnableJpaRepositories(value = "com.formreleaf.repository", repositoryFactoryBeanClass = FormReleafRepositoryFactoryBean.class)
@EnableTransactionManagement
@EnableSpringDataWebSupport
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Environment environment;

    @PostConstruct
    public void initApplication() throws IOException {
        if (environment.getActiveProfiles().length == 0) {
            LOGGER.warn("No spring profile configured, running with default configuration");
        } else {
            LOGGER.info("Running with Spring profile(s) : {}", Arrays.toString(environment.getActiveProfiles()));
            Collection activeProfiles = Arrays.asList(environment.getActiveProfiles());

            if (activeProfiles.contains("dev") && activeProfiles.contains("prod")) {
                LOGGER.error("You have misconfigured your application! " +
                        "It should not run with both the 'dev' and 'prod' profiles at the same time.");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Application.class);
        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        addDefaultProfile(app, source);
        Environment env = app.run(args).getEnvironment();

        LOGGER.info("Access URLs:\n----------------------------------------------------------\n\t" +
                        "Local: \t\thttp://127.0.0.1:{}\n\t" +
                        "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }

    private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        LOGGER.info("addDefaultProfile() ->  {}", source.containsProperty("spring.profiles.active"));

        if (!source.containsProperty("spring.profiles.active") &&
                !System.getenv().containsKey("SPRING_PROFILES_ACTIVE")) {

            app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);
        } else {
            String springProfilesActive = System.getenv().get("SPRING_PROFILES_ACTIVE");
            LOGGER.info("addDefaultProfile() -> active profile - {}", springProfilesActive);
        }
    }

    @Bean
    public FormProcessor formProcessor() {

        return new FormProcessor();
    }
}
