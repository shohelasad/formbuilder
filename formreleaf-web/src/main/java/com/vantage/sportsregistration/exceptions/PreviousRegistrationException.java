package com.vantage.sportsregistration.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Bazlur Rahman Rokon
 * @date 5/21/15.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PreviousRegistrationException extends RuntimeException {
    public PreviousRegistrationException() {
    }

    public PreviousRegistrationException(String message) {
        super(message);
    }
}
