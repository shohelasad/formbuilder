package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.en.KStemFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;


@AnalyzerDef(name = "organizationSearchAnalyzer",
  tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
  filters = {
    @TokenFilterDef(factory = LowerCaseFilterFactory.class),
    @TokenFilterDef(factory = KStemFilterFactory.class)
  })
@Indexed
@JsonIgnoreProperties
@Entity
public class Organization extends BaseEntity<Long> implements NonDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
  
	@Field
	@Analyzer(definition = "organizationSearchAnalyzer")
    @NotNull
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String phone;

    @Size(max = 100)
    private String fax;

    @Field
    @Analyzer(definition = "organizationSearchAnalyzer")
    @JsonIgnore
    @Type(type="org.hibernate.type.StringClobType")
    private String description;

    @Field
    @Size(max = 200)
    private String slogan;

    @Field
    @Column(name = "website", length = 1024)
    private String website;

    private String slug;
    private Boolean deleted = Boolean.FALSE; // by default its false

   
    @IndexedEmbedded
    @OneToMany(mappedBy = "organization")
    private Set<Program> programs = new HashSet<>();

    @IndexedEmbedded
    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", unique = true, nullable = true, insertable = true, updatable = true)
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Policy> policies = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Agreement> agreements = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<User> users = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Report> reports = new HashSet<>();

    public Organization() {
    }

    public Organization(String name, Address address, String phone, String fax, String website) {
        super();

        this.name = name;
        this.address = address;
        this.phone = phone;
        this.fax = fax;
        this.website = website;

        //this.userList.add(user);
    }

    @Override
    public String toString() {
        return "Organization [id=" + id + ", name=" + name + ", address="
                + address + ", phone=" + phone + ", fax=" + fax + ", website=" + website + "]";
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    @Override
    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public static class Builder {

        private Organization organization;

        public Builder() {
            organization = new Organization();
        }

        public Builder name(String name) {
            organization.name = name;
            return this;
        }

        public Builder user(User user) {
            organization.users.add(user);
            return this;
        }

        public Builder address(Address address) {
            organization.address = address;
            return this;
        }

        public Builder phone(String phone) {
            organization.phone = phone;
            return this;
        }

        public Builder fax(String fax) {
            organization.fax = fax;
            return this;
        }

        public Builder website(String website) {
            organization.website = website;
            return this;
        }

        public Builder id(Long id) {
            organization.id = id;
            return this;
        }

        public Organization build() {
            return organization;
        }

    }

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

    public void addUser(User user) {
        this.users.add(user);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Set<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(Set<Program> programs) {
        this.programs = programs;
    }

    public Set<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(Set<Policy> policies) {
        this.policies = policies;
    }

    public Set<Agreement> getAgreements() {
        return agreements;
    }

    public void setAgreements(Set<Agreement> agreements) {
        this.agreements = agreements;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }
}
