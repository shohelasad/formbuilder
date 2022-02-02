package com.vantage.sportsregistration.security;

import com.formreleaf.domain.Role;
import com.formreleaf.common.utils.SecurityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class SecurityAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAuthenticationSuccessHandler.class);
    
    protected static final String SAVED_REQUEST = "url_prior_auth";


    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {

        LOGGER.trace("onAuthenticationSuccess() login successful with login={}, sid={}", authentication.getName(), RequestContextHolder.currentRequestAttributes().getSessionId());

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains(Role.ROLE_APPLICATION.name())) {
            LOGGER.debug("ROLE_APPLICATION found, redirecting to application page");
            //here we will redirect user to respective page
            httpServletResponse.sendRedirect(getPreviousPageByRequest(httpServletRequest).orElse("/admin/organization/list"));
        }
        else if (roles.contains(Role.ROLE_ADMIN.name())) {  	
        	checkImpersonateUser(authentication);
        	
            LOGGER.debug("ROLE_ADMIN found, redirecting to organization user page");
            // here we will redirect user to respective page
            httpServletResponse.sendRedirect(getPreviousPageByRequest(httpServletRequest).orElse("/program/list"));
        } 
        else if (roles.contains(Role.ROLE_USER.name())) {	
        	checkImpersonateUser(authentication);
        	
            LOGGER.debug("ROLE_USER found, redirecting to user page");
            // here we will redirect user to respective page
            httpServletResponse.sendRedirect(getPreviousPageByRequest(httpServletRequest).orElse("/registration/list"));
        }
    }   
    
    private void checkImpersonateUser( Authentication authentication){
    	if(SecurityUtils.isSwitchedUser()){
    		LOGGER.info("[event:IMPERSONATE_USER] Application Admin: Formreleaf is impersonating to user={} ", authentication.getName());
    	}
    }
    
    private Optional<String> getPreviousPageByRequest(HttpServletRequest request)
    {
    	HttpSession session = request.getSession();
    	String redirectUrl = (String) session.getAttribute(SAVED_REQUEST);
        if (redirectUrl != null) {
           session.removeAttribute(SAVED_REQUEST);
        }
        
    	return Optional.ofNullable(redirectUrl).map(url -> url.toString());
    }    
}
