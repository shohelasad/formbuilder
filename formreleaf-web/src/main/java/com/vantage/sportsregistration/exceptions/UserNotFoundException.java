package com.vantage.sportsregistration.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Bazlur Rahman Rokon
 * @date 6/19/15.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserNotFoundException() {
        super("User may not found or locked or not active");
    }
}
