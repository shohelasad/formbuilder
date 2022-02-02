package com.vantage.sportsregistration.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/12/15.
 */
public class FormTemplate {
    @NotNull
    private Long programId;

    @NotEmpty
    private String template;

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
