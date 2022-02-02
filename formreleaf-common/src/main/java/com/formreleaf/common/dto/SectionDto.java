package com.formreleaf.common.dto;

/**
 * @author Bazlur Rahman Rokon
 * @date 7/12/15.
 */
public class SectionDto {
    private Long id;
    private String name;
    private boolean selected;

    public Long getId() {
        return id;
    }

    public SectionDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SectionDto setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public SectionDto setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }
}
