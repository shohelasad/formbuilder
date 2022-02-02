package com.formreleaf.common.dto;

/**
 * @author Bazlur Rahman Rokon
 * @since 7/13/15.
 */
public enum GeneralField {
    REGISTRATION_STATUS("Registration Status"),
    REGISTRATION_DATE("Registration Date"),
    APPROVAL("Approval"),
    PROGRAM_NAME("Program Name"),
    SECTION_NAME("Section Name");

    private String name;

    GeneralField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
