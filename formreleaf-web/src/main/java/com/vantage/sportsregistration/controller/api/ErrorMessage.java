package com.vantage.sportsregistration.controller.api;

import java.util.*;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/12/15.
 */
public class ErrorMessage {
    private List<String> errors;
    private Map<String, String> fieldErrors = new HashMap<>();

    public ErrorMessage() {
    }

    public ErrorMessage(List<String> errors) {
        this.errors = errors;
    }

    public ErrorMessage(List<String> errors, Map<String, String> fieldErrors) {
        this.errors = errors;
        this.fieldErrors = fieldErrors;
    }

    public ErrorMessage(String error) {
        this(Collections.singletonList(error));
    }

    public ErrorMessage(String... errors) {
        this(Arrays.asList(errors));
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
