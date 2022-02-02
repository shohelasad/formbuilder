package com.formreleaf.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bazlur Rahman Rokon
 * @date 7/10/15.
 */
@JsonPropertyOrder({"id", "sections"})
public class ProgramDto implements Serializable {
    private Long id;
    private String name;
    private boolean selected;

    @JsonProperty(value = "sections")
    private List<SectionDto> sectionDtos = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public ProgramDto setId(Long id) {
        this.id = id;
        return this;
    }

    public List<SectionDto> getSectionDtos() {
        return sectionDtos;
    }

    public ProgramDto setSectionDtos(List<SectionDto> sectionDtos) {
        this.sectionDtos = sectionDtos;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProgramDto setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public ProgramDto setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }
}
