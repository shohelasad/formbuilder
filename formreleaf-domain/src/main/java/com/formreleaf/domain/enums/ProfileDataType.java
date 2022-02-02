package com.formreleaf.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Bazlur Rahman Rokon
 * @date 6/4/15.
 */
public enum ProfileDataType {
    PARTICIPANT("participant"), ADDRESS("address"), MEDICATION("medication"), PARENT("parent"), CONTACT("contact"), INSURANCE("insurance"), PHYSICIAN("physician"), CONCERN("concern"), SECOND_PARENT("second_parent");

    public String name;

    ProfileDataType(String label) {
        this.name = label;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
