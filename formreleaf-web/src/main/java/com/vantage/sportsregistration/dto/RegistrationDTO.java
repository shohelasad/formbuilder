package com.vantage.sportsregistration.dto;

import com.formreleaf.domain.enums.RegistrationApproval;
import com.formreleaf.domain.enums.RegistrationStatus;

import java.util.Date;

/**
 * @author Bazlur Rahman Rokon
 * @since 6/6/15.
 */
public class RegistrationDTO {
    private Long id;
    private String registrantName;
    private String sectionNames;
    private String organizationName;
    private String programName;
    private String organizationSlug;
    private String programSlug;
    private String registrationDate;
    private boolean selected;
    private Date lastModifiedDate;
    private RegistrationStatus registrationStatus;
    private RegistrationApproval registrationApproval;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrantName() {
        return registrantName;
    }

    public void setRegistrantName(String registrantName) {
        this.registrantName = registrantName;
    }

    public String getSectionNames() {
        return sectionNames;
    }

    public void setSectionNames(String sectionNames) {
        this.sectionNames = sectionNames;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getOrganizationSlug() {
        return organizationSlug;
    }

    public void setOrganizationSlug(String organizationSlug) {
        this.organizationSlug = organizationSlug;
    }

    public String getProgramSlug() {
        return programSlug;
    }

    public void setProgramSlug(String programSlug) {
        this.programSlug = programSlug;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public RegistrationApproval getRegistrationApproval() {
        return registrationApproval;
    }

    public void setRegistrationApproval(RegistrationApproval registrationApproval) {
        this.registrationApproval = registrationApproval;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public RegistrationDTO setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }

    @Override
    public String toString() {
        return "RegistrationDTO{" +
                "id=" + id +
                ", registrantName='" + registrantName + '\'' +
                ", sectionNames='" + sectionNames + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", programName='" + programName + '\'' +
                ", organizationSlug='" + organizationSlug + '\'' +
                ", programSlug='" + programSlug + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", selected=" + selected +
                ", registrationStatus=" + registrationStatus +
                ", registrationApproval=" + registrationApproval +
                '}';
    }
}
