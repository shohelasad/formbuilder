package com.formreleaf.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Bazlur Rahman Rokon
 * @date 7/11/15.
 */

public class FieldDto implements Comparable<FieldDto>, Serializable {
    @JsonProperty
    private Long index; //placeHolder

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private boolean selected;

    @JsonProperty
    private Long fieldId;

    @JsonProperty
    private FieldDtoType fieldDtoType;

    public FieldDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public FieldDto() {
    }

    public String getId() {
        return id;
    }

    public FieldDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public FieldDto setName(String name) {
        this.name = name;
        return this;
    }

    public Long getIndex() {
        return index;
    }

    public FieldDto setIndex(Long index) {
        this.index = index;
        return this;
    }

    @Override
    public int compareTo(FieldDto o) {
        return this.id.compareTo(o.getId());
    }

    public boolean isSelected() {
        return selected;
    }

    public FieldDto setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public FieldDto setFieldId(Long fieldId) {
        this.fieldId = fieldId;
        return this;
    }

    public FieldDtoType getFieldDtoType() {
        return fieldDtoType;
    }

    public FieldDto setFieldDtoType(FieldDtoType fieldDtoType) {
        this.fieldDtoType = fieldDtoType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldDto fieldDto = (FieldDto) o;
        return Objects.equals(name, fieldDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {

        return "index=" + index +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", selected=" + selected;
    }
}
