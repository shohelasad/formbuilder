package com.formreleaf.common.dto;

import java.util.Objects;

/**
 * @author Bazlur Rahman Rokon
 * @date 6/7/15.
 */
public class PeopleDTO {
    private String person;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String gender;
    private String dateOfBirth;
    private String homePhone;
    private String cellPhone;
    private Long registrationCount;

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getFirstName() {
        return firstName;
    }

    public PeopleDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public PeopleDTO setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PeopleDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
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

    public Long getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(Long registrationCount) {
        this.registrationCount = registrationCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeopleDTO peopleDTO = (PeopleDTO) o;
        return Objects.equals(person, peopleDTO.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person);
    }

    @Override
    public String toString() {
        return "PeopleDTO{" +
                "person='" + person + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", homePhone='" + homePhone + '\'' +
                ", cellPhone='" + cellPhone + '\'' +
                ", registrationCount=" + registrationCount +
                '}';
    }
}
