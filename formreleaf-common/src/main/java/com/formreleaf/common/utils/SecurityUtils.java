package com.formreleaf.common.utils;

import java.util.Set;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtils {

    public static boolean isSwitchedUser() {
    
        return hasRole("ROLE_PREVIOUS_ADMINISTRATOR");
    }
    
    public static boolean hasRole(String role) {
    	
        return getAuthorities().contains(role);
    }

    private static Set<String> getAuthorities() {
    	
        return AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }
    
}
