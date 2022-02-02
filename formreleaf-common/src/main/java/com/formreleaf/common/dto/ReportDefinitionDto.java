package com.formreleaf.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bazlur Rahman Rokon
 * @date 7/9/15.
 */

@JsonTypeName(value = "ReportFilter")
public class ReportDefinitionDto implements Serializable {
    private Long id;
    private String name;

    @JsonProperty("programs")
    private List<ProgramDto> programs = new ArrayList<>();

    @JsonProperty("registrationDate")
    private RegistrationDateDto registrationDate;

    private String registrantName;

    @JsonProperty("selectedBlocks")
    private List<BlockDto> selectedBlocks;

    @JsonProperty("approvals")
    private List<ApprovalDto> approvalDtos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ApprovalDto> getApprovalDtos() {
        return approvalDtos;
    }

    public ReportDefinitionDto setApprovalDtos(List<ApprovalDto> approvalDtos) {
        this.approvalDtos = approvalDtos;
        return this;
    }

    public List<ProgramDto> getPrograms() {
        return programs;
    }

    public ReportDefinitionDto setPrograms(List<ProgramDto> programs) {
        this.programs = programs;
        return this;
    }

    public RegistrationDateDto getRegistrationDate() {
        return registrationDate;
    }

    public ReportDefinitionDto setRegistrationDate(RegistrationDateDto registrationDate) {
        this.registrationDate = registrationDate;
        return this;
    }

    public String getRegistrantName() {
        return registrantName;
    }

    public ReportDefinitionDto setRegistrantName(String registrantName) {
        this.registrantName = registrantName;
        return this;
    }

    public List<BlockDto> getSelectedBlocks() {
        return selectedBlocks;
    }

    public ReportDefinitionDto setSelectedBlocks(List<BlockDto> selectedBlocks) {
        this.selectedBlocks = selectedBlocks;
        return this;
    }
}
