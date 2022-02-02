package com.vantage.sportsregistration.exceptions;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/10/15.
 */
public class DocumentUploadException extends RuntimeException {
    public DocumentUploadException(String message, Exception exception) {
        super(message, exception);
    }

    public DocumentUploadException(String message) {
        super(message);
    }

    public DocumentUploadException() {
    }
}
