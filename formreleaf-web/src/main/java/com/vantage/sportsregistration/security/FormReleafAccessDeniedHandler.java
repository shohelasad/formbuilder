package com.vantage.sportsregistration.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Bazlur Rahman Rokon
 * @since 8/19/15.
 */
@Component
public class FormReleafAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormReleafAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LOGGER.error("accessDenied", accessDeniedException);

        response.sendRedirect("/accessDenied");
    }
}
