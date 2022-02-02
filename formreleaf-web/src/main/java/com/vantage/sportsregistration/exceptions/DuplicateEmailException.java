package com.vantage.sportsregistration.exceptions;

public class DuplicateEmailException extends Exception {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
