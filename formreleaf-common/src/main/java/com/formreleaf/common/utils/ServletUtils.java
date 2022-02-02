package com.formreleaf.common.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author Bazlur Rahman Rokon
 * @since 7/13/15.
 */
public class ServletUtils {
    public static String getContextURL(String appendPath) {

        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(appendPath)
                .build()
                .toUriString();
    }

    public static String getDefaultContextURL(String appendPath, String profile) {
        switch (profile) {
            case Constants.SPRING_PROFILE_STAGING:
                return "http://staging.formreleaf.com/" + appendPath;

            case Constants.SPRING_PROFILE_PRODUCTION:
                return "http://formreleaf.com/" + appendPath;

            default:
                return "http://localhost:8080/" + appendPath;
        }
    }

}
