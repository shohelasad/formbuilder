package com.vantage.sportsregistration;

import com.formreleaf.common.forms.FormProcessor;
import com.formreleaf.common.utils.Constants;
import com.formreleaf.config.FormReleafRepositoryFactoryBean;
import com.vantage.sportsregistration.config.AppConfig;
import com.vantage.sportsregistration.movie.MovieDao;
import com.vantage.sportsregistration.movie.MovieDaoImpl;
import com.vantage.sportsregistration.service.UserService;
import com.vantage.sportsregistration.service.UserServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;

@ComponentScan(basePackages = {"com.formreleaf.report.service", "com.vantage.sportsregistration"})
@EnableAutoConfiguration
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
        
       /* ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MovieDao obj = (MovieDao) context.getBean("movieDao");
	    
        LOGGER.info("Result : {}", obj.findByDirector("a"));
        LOGGER.info("Result : {}", obj.findByDirector("a"));
        LOGGER.info("Result : {}", obj.findByDirector("c"));*/
        
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
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return (container -> {
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND,
                    "/resourceNotFound.html");

            container.addErrorPages(error404Page);
        });
    }

    @Bean
    public FormProcessor formProcessor() {

        return new FormProcessor();
    }
}
