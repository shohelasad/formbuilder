package com.vantage.sportsregistration.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Bazlur Rahman Rokon
 * @since 10/6/15.
 */
public class UserAgentFilter implements Filter {
    private static Logger log = LoggerFactory.getLogger(UserAgentFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        final String header = request.getHeader("user-agent");

        log.info("[event:USER_AGENT] {}", header);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
