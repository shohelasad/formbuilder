package com.formreleaf.domain.enums;

/**
 * @author Bazlur Rahman Rokon
 * @since 6/6/15.
 */
public enum RegistrationApproval {
    APPROVED("Approved"), NOT_APPROVED("Not Approved"), NONE("None");

    private String label;

    RegistrationApproval(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
