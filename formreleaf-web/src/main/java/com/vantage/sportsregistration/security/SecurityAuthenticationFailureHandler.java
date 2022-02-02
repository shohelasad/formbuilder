package com.vantage.sportsregistration.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/5/15.
 */
public class SecurityAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String userName = request.getParameter("username");
        LOGGER.info("onAuthenticationFailure- username={}, exceptionClass={}", userName, exception.getClass().getName());

        String parameter = "";

        if (exception instanceof UsernameNotFoundException) {
            parameter = "usernameEmpty";
        } else if (exception instanceof BadCredentialsException) {
            parameter = "badCredential";
        } else if (exception instanceof LockedException) {
            parameter = "userLocked";
        } else if (exception instanceof DisabledException) {
            parameter = "userNotActivated" + "?userEmail=" + userName;
        }

        response.sendRedirect("/login-error/" + parameter);
    }
}
