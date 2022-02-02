package com.formreleaf.common.forms;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/10/15.
 */
public enum FieldType {
    TEXTFIELD("textfield"),
    RADIO("radio"),
    DROPDOWN("dropdown"),
    DATE("date"),
    CHECKBOX("checkbox"),
    TEXTAREA("textarea"),
    YES_NO_COMMENT("yesNoComment"),
    COUNTRY("country"),
    STATE("state"),
    FILE_UPLOAD("fileupload");

    public String name;

    FieldType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
