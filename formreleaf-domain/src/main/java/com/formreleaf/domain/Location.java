package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.formreleaf.common.utils.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;


@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Size(max = 100)
    private String name;

    @NotEmpty
    @Size(max = 200)
    private String addressLine1;

    @Size(max = 200)
    private String addressLine2;

    @Size(max = 100)
    private String city;

    @Size(max = 20)
    //@Pattern(regexp = "^$|^\\d{5}(?:[-\\s]\\d{4})?$", message = "Zip Code is not valid")
    private String zip;

    private String state;

    @Size(max = 100)
    private String country;

    @JsonIgnore
    @ManyToOne
    private Program program;

    private boolean enabled = true;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("<address>");
        sb.append("<strong>")
                .append(name)
                .append("</strong>")
                .append("<br/>")
                .append(addressLine1);
        if (StringUtils.isNotEmpty(addressLine2)) {
            sb.append("<br/>").append(addressLine2);
        }
        if (StringUtils.isNotEmpty(city)) {
            sb.append("<br/>").append(city);
        }
        if (StringUtils.isNotEmpty(state)) {
            sb.append(", ").append(state);
        }
        if (StringUtils.isNotEmpty(zip)) {
            sb.append(", ").append(zip);
        }
        sb.append("<br/>").append(country);

        return sb.toString();
    }
}
