package com.formreleaf.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Bazlur Rahman Rokon
 * @date 8/7/15.
 */
public enum BlockDtoType {
    AGREEMENT("Agreement"), POLICY("Policy"), GENERAL("General"), FIELD("Field");

    private String label;

    BlockDtoType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static BlockDtoType fromString(String key) {
        return key == null
                ? null
                : BlockDtoType.valueOf(key.toUpperCase());
    }
}
