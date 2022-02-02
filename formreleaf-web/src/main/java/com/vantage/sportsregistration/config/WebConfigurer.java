package com.vantage.sportsregistration.config;

import com.formreleaf.common.utils.Constants;
import com.vantage.sportsregistration.web.filter.CachingHttpHeadersFilter;
import com.vantage.sportsregistration.web.filter.MdcFilter;
import com.vantage.sportsregistration.web.filter.UserAgentFilter;
import com.vantage.sportsregistration.web.filter.gzip.GZipServletFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bazlur Rahman Rokon
 * @date 6/13/15.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, EmbeddedServletContainerCustomizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebConfigurer.class);

    @Autowired
    private Environment env;

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        mappings.add("html", "text/html;charset=utf-8");
        mappings.add("json", "text/html;charset=utf-8");
        mappings.add("js", "application/javascript;charset=utf-8");
        mappings.add("woff", "application/font-woff;charset=utf-8");
        container.setMimeMappings(mappings);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        LOGGER.info("Web application configuration ");
        EnumSet<DispatcherType> dispatcherTypesps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);

        initUserAgentDetection(servletContext, dispatcherTypesps);

        if (env.acceptsProfiles(Constants.SPRING_PROFILE_PRODUCTION)) {
            initGzipFilter(servletContext, dispatcherTypesps);
            initMdcFilter(servletContext, dispatcherTypesps);
            initCachingHttpHeadersFilter(servletContext, dispatcherTypesps);
        }
        LOGGER.info("Web application fully configured");
    }

    private void initUserAgentDetection(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        FilterRegistration.Dynamic mdcFilter = servletContext.addFilter("userAgentFilter", new UserAgentFilter());
        mdcFilter.addMappingForUrlPatterns(disps, true, "/login");
    }

    private void initGzipFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        LOGGER.info("Registering GZip Filter");
        FilterRegistration.Dynamic compressingFilter = servletContext.addFilter("gzipFilter", new GZipServletFilter());
        Map<String, String> parameters = new HashMap<>();
        compressingFilter.setInitParameters(parameters);
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.css");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.json");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.html");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.js");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.svg");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.ttf");
        compressingFilter.addMappingForUrlPatterns(disps, true, "/api/*");
        compressingFilter.setAsyncSupported(true);
    }

    private void initMdcFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        LOGGER.info("Registering MDC Filter");
        FilterRegistration.Dynamic mdcFilter = servletContext.addFilter("mdcFilter", new MdcFilter());
        mdcFilter.addMappingForUrlPatterns(disps, true, "/*");
    }

    /**
     * Initializes the cachig HTTP Headers Filter.
     */
    private void initCachingHttpHeadersFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        LOGGER.info("Registering Caching HTTP Headers Filter");
        FilterRegistration.Dynamic cachingHttpHeadersFilter =
                servletContext.addFilter("cachingHttpHeadersFilter", new CachingHttpHeadersFilter(env));

        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "*.css");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "*.js");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/images/*");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/img/*");
        cachingHttpHeadersFilter.setAsyncSupported(true);
    }
}
