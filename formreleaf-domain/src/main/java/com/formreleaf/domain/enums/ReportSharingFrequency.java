package com.formreleaf.domain.enums;

/**
 * @author Bazlur Rahman Rokon
 * @since 8/3/15.
 */
public enum ReportSharingFrequency {
    DAILY("Daily"), WEEKLY("Weekly"), MONTHLY("Monthly");

    private String label;

    ReportSharingFrequency(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
