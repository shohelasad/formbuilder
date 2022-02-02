package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.formreleaf.common.utils.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Size(min = 2, max = 50)
    private String firstName;

    @NotEmpty
    @Size(min = 2, max = 50)
    private String lastName;

    @NotEmpty
    @Size(max = 200)
    private String title;

    @Size(max = 20)
    private String workPhone;

    @Size(max = 20)
    private String cellPhone;

    @Email
    @Size(max = 100)
    private String email;

    @JsonIgnore
    @ManyToOne
    private Program program;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("<address>")
                .append("<strong>")
                .append(firstName)
                .append(" ")
                .append(lastName)
                .append("</strong>")
                .append("<br/>").append(title);

        if (StringUtils.isNotEmpty(workPhone)) {
            builder.append("<br/>")
                    .append(workPhone);
        }

        if (StringUtils.isNotEmpty(cellPhone)) {
            builder.append("<br/>")
                    .append(cellPhone);
        }

        if (StringUtils.isNotEmpty(email)) {
            builder.append("<br/>")
                    .append(email);
        }

        builder.append("</address>");

        return builder.toString();
    }
}
