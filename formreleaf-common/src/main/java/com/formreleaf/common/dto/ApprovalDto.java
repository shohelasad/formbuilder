package com.formreleaf.common.dto;

/**
 * @author Bazlur Rahman Rokon
 * @date 7/12/15.
 */
public class ApprovalDto {
    private String name;
    private boolean selected;

    public String getName() {
        return name;
    }

    public ApprovalDto setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public ApprovalDto setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }
}
