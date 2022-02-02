package com.formreleaf.domain.enums;

public enum RegistrationStatus {

    COMPLETED("Completed"), INCOMPLETE("Incomplete"), CANCELED("Canceled");

    private String label;

    RegistrationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
