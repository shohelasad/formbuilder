package com.vantage.sportsregistration.web.filter;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Bazlur Rahman Rokon
 * @since 6/25/15.
 * <p>
 * mapped diagnostic context filter
 */
public class MdcFilter implements Filter {
    private static final String IP_ADDRESS = "ipAddress";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //ignore
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        MDC.put("sid", RequestContextHolder.currentRequestAttributes().getSessionId());
        MDC.put(IP_ADDRESS, request.getRemoteAddr());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            MDC.put("user", auth.getName());
        } else {
            MDC.put("user", "system");
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("sid");
            MDC.remove("user");
            MDC.remove(IP_ADDRESS);
        }
    }

    @Override
    public void destroy() {

    }
}
