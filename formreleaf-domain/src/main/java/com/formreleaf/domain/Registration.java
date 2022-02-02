package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.formreleaf.domain.enums.RegistrationApproval;
import com.formreleaf.domain.enums.RegistrationStatus;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collector;


@SqlResultSetMapping(name = "Registration", classes = {@ConstructorResult(targetClass = Registration.class, columns = {
        @ColumnResult(name = "formData", type = String.class)})})

@TypeDefs({@TypeDef(name = "StringJsonbObject", typeClass = StringJsonbUserType.class)})
@Entity
@JsonIgnoreProperties
public class Registration extends BaseEntity<Long> implements NonDeletable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Date registrationDate;

    //TODO:revisit & remove it
    // Ideally we are going to have to replace this field with firstName, lastName, and middleName,
    // but for now keeping this as already a huge set of data inserted in this field
    private String registrantName;

    @Size(max = 100)
    private String firstName; // registrant's first name

    @Size(max = 100)
    private String middleName;// registrant's last name

    @Size(max = 100)
    private String lastName;  // registrant's last name
    private String email;
    private String gender;
    private String dateOfBirth;
    private String homePhone;
    private String cellPhone;
    private boolean deleted;

    @Type(type = "StringJsonbObject")
    @Column(nullable = false)
    private String formData;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private RegistrationStatus registrationStatus = RegistrationStatus.INCOMPLETE;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private RegistrationApproval registrationApproval = RegistrationApproval.NONE;

    @JsonIgnore
    @ManyToOne
    private Program program;

    @JsonIgnore
    @ManyToOne
    private User registrant;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "registration_sections",
            joinColumns = {@JoinColumn(name = "registration_id", referencedColumnName = "id", unique = false)},
            inverseJoinColumns = {@JoinColumn(name = "section_id", referencedColumnName = "id", unique = false)})
    private Set<Section> sections = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "registration_signatures",
            joinColumns = {@JoinColumn(name = "registration_id", referencedColumnName = "id", unique = false)},
            inverseJoinColumns = {@JoinColumn(name = "signature_id", referencedColumnName = "id", unique = false)})
    private Set<Signature> signatures = new TreeSet<>();

    @Transient
    private boolean selected;

    public Registration() {
    }

    public Registration(String formData) {
        this.formData = formData;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public User getRegistrant() {
        return registrant;
    }

    public void setRegistrant(User registrant) {
        this.registrant = registrant;
    }

    public String getFirstName() {
        return firstName;
    }

    public Registration setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public Registration setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Registration setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public Set<Section> getSections() {
        return sections;
    }

    public void setSections(Set<Section> sections) {
        this.sections = sections;
    }

    public Set<Signature> getSignatures() {
        return signatures;
    }

    public void setSignatures(Set<Signature> signatures) {
        this.signatures = signatures;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getRegistrantName() {
        return registrantName;
    }

    public void setRegistrantName(String registrantName) {
        this.registrantName = registrantName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public RegistrationApproval getRegistrationApproval() {
        return registrationApproval;
    }

    public void setRegistrationApproval(RegistrationApproval registrationApproval) {
        this.registrationApproval = registrationApproval;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getSectionNames() {

        Collector<Section, StringJoiner, String> sectionJoiner = Collector.of(() -> new StringJoiner(", "),
                (joiner, section) -> joiner.add(section.getName()),
                StringJoiner::merge, StringJoiner::toString);

        return getSections()
                .stream()
                .collect(sectionJoiner);
    }

    @Override
    public Boolean isDeleted() {
        return this.deleted;
    }

    @Override
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Registration{" +
                "registrationDate=" + registrationDate +
                ", id=" + id +
                ", registrantName='" + registrantName + '\'' +
                ", registrationApproval=" + registrationApproval +
                ", registrationStatus=" + registrationStatus +
                '}';
    }
}
