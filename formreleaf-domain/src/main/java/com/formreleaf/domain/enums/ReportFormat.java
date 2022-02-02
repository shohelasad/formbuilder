package com.formreleaf.domain.enums;

/**
 * @author Bazlur Rahman Rokon
 * @since 8/3/15.
 */
public enum ReportFormat {
    PDF("Pdf Format"), CSV("Csv Format");

    private String label;

    ReportFormat(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
