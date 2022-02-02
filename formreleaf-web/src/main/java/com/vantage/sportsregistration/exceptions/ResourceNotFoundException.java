package com.vantage.sportsregistration.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/12/15.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("Resource not found in database");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String message, Long id) {
        super(message + id);
    }

    public ResourceNotFoundException(String message, String others) {
        super(message + others);
    }

}
