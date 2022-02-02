package com.formreleaf.common.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Field implements Cloneable {
    private String id;
    private FieldType fieldType;
    private String name;
    private String title;
    private String helpText;
    private boolean selected;
    private String value;
    private Validator validator;
    private boolean custom;
    private List<Option> options = new ArrayList<>();

    public Field() {
    }
    
	public Field(String id, FieldType fieldType, String title, String helpText) {
        this.id = id;
        this.fieldType = fieldType;
        this.title = title;
        this.helpText = helpText;
    }

    public Field(String id, FieldType fieldType, String title) {
        this.id = id;
        this.fieldType = fieldType;
        this.title = title;
        this.helpText = "Please enter " + title.toLowerCase();
    }

    public Field(String id, FieldType fieldType, String title, boolean required) {
        this(id, fieldType, title);
        this.validator = new Validator(required);
    }

    public Field(FieldType fieldType, String title, String helpText) {
        this.fieldType = fieldType;
        this.title = title;
        this.helpText = helpText;
    }

    public Field(FieldType fieldType, String title) {
        this.fieldType = fieldType;
        this.title = title;
    }

    public Field(FieldType fieldType, String title, boolean required) {
        this(fieldType, title);
        this.validator = new Validator(required);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCustom() {
        return custom;
    }

    public Field setCustom(boolean custom) {
        this.custom = custom;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;

        return Objects.equals(selected, field.selected) &&
                Objects.equals(id, field.id) &&
                Objects.equals(fieldType, field.fieldType) &&
                Objects.equals(name, field.name) &&
                Objects.equals(value, field.value) &&
                Objects.equals(options, field.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fieldType, name, selected, options);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Field{");
        sb.append(", fieldType=").append(fieldType);
        sb.append(", name='").append(name).append('\'');
        sb.append(", selected=").append(selected);
        sb.append(", value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
