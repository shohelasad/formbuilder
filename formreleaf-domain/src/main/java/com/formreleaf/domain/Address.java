package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.formreleaf.common.utils.StringUtils;
import org.hibernate.search.annotations.Field;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Embeddable
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Field
    @NotEmpty
    @Size(max = 100)
    private String name;

    @NotEmpty
    @Size(max = 200)
    private String addressLine1;

    @Size(max = 200)
    private String addressLine2;

    @Field
    @Size(max = 100)
    private String street;

    @Field
    @Size(max = 100)
    private String city;

    @Field
    @Size(max = 100)
    private String state;

    @Field
    @Size(max = 100)
    private String zip;

    @Field
    @Size(max = 100)
    private String country;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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


    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                '}';
    }

    public String getAddressAsHtml() {
        String address = "<address>";

        if (StringUtils.isNotEmpty(name)) {
            address += name;
        }

        if (StringUtils.isNotEmpty(addressLine1)) {
            address += "<br/>" + addressLine1;
        }

        if (StringUtils.isNotEmpty(addressLine2)) {
            address += "<br/>" + addressLine2;
        }

        if (StringUtils.isNotEmpty(street)) {
            address += "<br/>" + street;
        }
        if (StringUtils.isNotEmpty(city)) {
            address += " ," + city;
        }

        if (StringUtils.isNotEmpty(state)) {
            address += " ," + state;
        }

        if (StringUtils.isNotEmpty(zip)) {
            address += " ," + zip;
        }

        if (StringUtils.isNotEmpty(country)) {
            address += ("<br/>" + country);
        }

        address += "</address>";

        return address;
    }
}
