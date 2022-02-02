package com.formreleaf.common.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/18/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Validator {
    private Integer maxLength;
    private Integer minLength;
    private Boolean required;
    private String pattern;
    private ValidationType validationType;
    private String errorMessage;

    public Validator(ValidationType validationType, Boolean required, Integer minLength, Integer maxLength) {
        this.validationType = validationType;
        this.required = required;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public Validator(ValidationType validationType, Boolean required) {
        this.validationType = validationType;
        this.required = required;
    }

    public Validator(boolean required) {
        this.required = required;
        this.validationType = ValidationType.TEXT;
    }

    public Validator() {
    }

    public Validator(Boolean required, Integer minLength, Integer maxLength) {
        this.required = required;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public Validator(Integer minLength, Integer maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationType validationType) {
        this.validationType = validationType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
