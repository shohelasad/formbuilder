package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@SqlResultSetMapping(name = "Program", classes = {@ConstructorResult(targetClass = Program.class, columns = {
        @ColumnResult(name = "id", type = Long.class),
        @ColumnResult(name = "formTemplate", type = String.class)})})
@TypeDefs({@TypeDef(name = "StringJsonbObject", typeClass = StringJsonbUserType.class)})
@Entity
public class Program extends BaseEntity<Long> implements NonDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Field
    @Size(max = 200)
    @NotNull
    @Column(length = 256, nullable = false)
    private String name;

    @Field
    @JsonIgnore
    @Lob
    private String description;

    private long registrationCount;

    private Boolean deleted = Boolean.FALSE;

    @Size(max = 256)
    private String slug;

    @JsonIgnore
    @Lob
    @Column(length = 1000000)
    private byte[] image;

    @ContainedIn
    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Section> sections = new HashSet<>();

    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Location> locations = new HashSet<>();

    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Contact> contacts = new HashSet<>();

    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Registration> registrations = new HashSet<>();

    @Field
    private String tag;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Publish publish;

    @Type(type = "StringJsonbObject")
    private String formTemplate;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "program_policies",
            joinColumns = @JoinColumn(name = "program_id", referencedColumnName = "id", unique = false),
            inverseJoinColumns = @JoinColumn(name = "policy_id", referencedColumnName = "id", unique = false))
    private Set<Policy> policies = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "program_agreements",
            joinColumns = {@JoinColumn(name = "program_id", referencedColumnName = "id", unique = false)},
            inverseJoinColumns = {@JoinColumn(name = "agreement_id", referencedColumnName = "id", unique = false)})
    private Set<Agreement> agreements = new HashSet<>();

    public Program() {
    }

    public Program(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Program(Long id, String formTemplate) {
        this.id = id;
        this.formTemplate = formTemplate;
    }

    @Override
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(long registrationCount) {
        this.registrationCount = registrationCount;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getFormTemplate() {
        return formTemplate;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public Set<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Publish getPublish() {
        return publish;
    }

    public void setPublish(Publish publish) {
        this.publish = publish;
    }

    public void setFormTemplate(String formTemplate) {
        this.formTemplate = formTemplate;
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

    public Set<Section> getSections() {
        return sections;
    }

    public void setSections(Set<Section> sections) {
        this.sections = sections;
    }

    public Set<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(Set<Registration> registrations) {
        this.registrations = registrations;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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
        Program program = (Program) o;
        return Objects.equals(id, program.id) &&
                Objects.equals(name, program.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Program{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", registrationCount=" + registrationCount +
                ", tag='" + tag + '\'' +
                ", publish=" + publish +
                ", formTemplate='" + formTemplate + '\'' +
                '}';
    }
}
