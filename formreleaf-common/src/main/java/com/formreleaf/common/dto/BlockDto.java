package com.formreleaf.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Bazlur Rahman Rokon
 * @date 7/11/15.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockDto implements Serializable {
    @JsonProperty
    private Long index; // placeholder

    @JsonProperty
    private String blockName;

    @JsonProperty
    private String sectionName;

    @JsonProperty
    private BlockDtoType blockDtoType;

    @JsonProperty(value = "fields")
    private Set<FieldDto> fieldDtos = new LinkedHashSet<>();

    public BlockDto() {
    }

    public BlockDto(String blockName, Set<FieldDto> fieldDtos) {
        this.blockName = blockName;
        this.fieldDtos = fieldDtos;
    }

    public String getBlockName() {
        return blockName;
    }

    public BlockDto setBlockName(String blockName) {
        this.blockName = blockName;
        return this;
    }

    public String getSectionName() {
        return sectionName;
    }

    public BlockDto setSectionName(String sectionName) {
        this.sectionName = sectionName;
        return this;
    }

    public Set<FieldDto> getFieldDtos() {
        return fieldDtos;
    }

    public BlockDto setFieldDtos(Set<FieldDto> fieldDtos) {
        this.fieldDtos = fieldDtos;
        return this;
    }

    public Long getIndex() {
        return index;
    }

    public BlockDto setIndex(Long index) {
        this.index = index;
        return this;
    }

    public BlockDtoType getBlockDtoType() {
        return blockDtoType;
    }

    public BlockDto setBlockDtoType(BlockDtoType blockDtoType) {
        this.blockDtoType = blockDtoType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockDto blockDto = (BlockDto) o;
        return Objects.equals(blockName, blockDto.blockName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockName);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BlockDto{");
        sb.append("blockName='").append(blockName).append('\'');
        sb.append(", blockDtoType=").append(blockDtoType);
        sb.append(", fieldDtos=").append(fieldDtos);
        sb.append('}');
        return sb.toString();
    }
}