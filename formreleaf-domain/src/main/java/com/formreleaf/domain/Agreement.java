package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Agreement extends BaseEntity<Long> implements NonDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Size(max = 100)
    private String title;

    @NotEmpty
    @Type(type = "org.hibernate.type.StringClobType")
    private String details;
    private Boolean mustBeAgreed;
    private Boolean deleted = false;

    @ManyToOne
    @JsonIgnore
    private Organization organization;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    private Registration registration;

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    @Transient
    private boolean selected = false; // selected agreement for program

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Boolean isMustBeAgreed() {
        return mustBeAgreed;
    }

    public void setMustBeAgreed(Boolean mustBeAgreed) {
        this.mustBeAgreed = mustBeAgreed;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agreement agreement = (Agreement) o;
        return Objects.equals(id, agreement.id) &&
                Objects.equals(title, agreement.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return "Agreement{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", details='" + details + '\'' +
                ", mustBeAgreed=" + mustBeAgreed +
                ", organization=" + organization +
                '}';
    }
}
