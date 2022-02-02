package com.formreleaf.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@TypeDefs({@TypeDef(name = "StringJsonbObject", typeClass = StringJsonbUserType.class)})
@Entity
@JsonIgnoreProperties
public class Report extends BaseEntity<Long> implements NonDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private boolean deleted;

    @Type(type = "StringJsonbObject")
    @Column(nullable = false)
    private String formData;

    @JsonIgnore
    @ManyToOne
    private Organization organization;

    @JsonIgnore
    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReportShareSchedule> reportShareSchedules = new HashSet<>();

    @Transient
    private long registrationCount;

    public Report setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Report setName(String name) {
        this.name = name;
        return this;
    }

    public Report setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public String getFormData() {
        return formData;
    }

    public Report setFormData(String formData) {
        this.formData = formData;
        return this;
    }

    public Organization getOrganization() {
        return organization;
    }

    public Report setOrganization(Organization organization) {
        this.organization = organization;
        return this;
    }

    public long getRegistrationCount() {
        return registrationCount;
    }

    public Report setRegistrationCount(long registrationCount) {
        this.registrationCount = registrationCount;
        return this;
    }

    public Set<ReportShareSchedule> getReportShareSchedules() {
        return reportShareSchedules;
    }

    public Report setReportShareSchedules(Set<ReportShareSchedule> reportShareSchedules) {
        this.reportShareSchedules = reportShareSchedules;
        return this;
    }

    @Override
    public Boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Report{" + "name='" + name + '\'' + ", id=" + id + ", deleted=" + deleted + '}';
    }
}
