package com.formreleaf.common.forms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/26/15.
 */
public enum ValidationType {
    NUMBER("number"), EMAIL("email"), PHONE("tel"), TEXT("text"), FILE("file");

    public String name;

    ValidationType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static ValidationType fromString(String key) {
        if (key == null) {
            return null;
        } else if ("tel".equalsIgnoreCase(key)) {
            return ValidationType.PHONE;
        } else {
            return ValidationType.valueOf(key.toUpperCase());
        }
    }
}
