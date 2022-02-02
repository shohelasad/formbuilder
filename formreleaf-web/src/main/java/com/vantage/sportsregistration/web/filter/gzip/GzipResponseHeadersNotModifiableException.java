package com.vantage.sportsregistration.web.filter.gzip;

import javax.servlet.ServletException;

/**
 * @author Bazlur Rahman Rokon
 * @date 6/13/15.
 */

public class GzipResponseHeadersNotModifiableException extends ServletException {

    public GzipResponseHeadersNotModifiableException(String message) {
        super(message);
    }
}
