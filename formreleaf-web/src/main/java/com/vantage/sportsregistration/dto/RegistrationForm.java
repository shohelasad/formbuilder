package com.vantage.sportsregistration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.formreleaf.domain.Signature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationForm {
    private Long id;
    private Long programId;
    private List<Long> sectionIds;
    private String formTemplate;
    private Set<Signature> signatures = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormTemplate() {
        return formTemplate;
    }

    public void setFormTemplate(String formTemplate) {
        this.formTemplate = formTemplate;
    }

    public Set<Signature> getSignatures() {
        return signatures;
    }

    public void setSignatures(Set<Signature> signatures) {
        this.signatures = signatures;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public List<Long> getSectionIds() {
        return sectionIds;
    }

    public void setSectionIds(List<Long> sectionIds) {
        this.sectionIds = sectionIds;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RegistrationForm{");
        sb.append("id=").append(id);
        sb.append(", programId=").append(programId);
        sb.append(", sectionIds=").append(sectionIds);
        sb.append(", formTemplate='").append(formTemplate).append('\'');
        sb.append(", signatures='").append(signatures).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
