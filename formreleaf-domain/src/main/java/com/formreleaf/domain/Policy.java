package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 4/26/15.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Policy extends BaseEntity<Long> implements NonDeletable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Size(max = 100)
    private String title;

    @NotEmpty
    @Type(type = "org.hibernate.type.StringClobType")
    private String details;
    private Boolean defaultPolicy;

    private Boolean deleted = false;

    @JsonIgnore
    @ManyToOne
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

    public Boolean isDefaultPolicy() {
        return defaultPolicy;
    }

    public void setDefaultPolicy(Boolean defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Override
    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Policy{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", details='" + details + '\'' +
                ", defaultPolicy=" + defaultPolicy +
                ", organization=" + organization +
                '}';
    }


}
