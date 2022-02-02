package com.vantage.sportsregistration.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;


public class OrganizationForm {

    public static final String FIELD_NAME_NAME = "name";

    private long id;

    @NotEmpty
    @Size(max = 100)
    private String name;

    @NotEmpty
    @Size(max = 100)
    private String phone;

    @Size(max = 100)
    private String fax;

    @Size(max = 100)
    private String website;

    //Address
    @NotEmpty
    @Size(max = 200)
    private String addressLine1;

    @Size(max = 200)
    private String addressLine2;

    @Size(max = 100)
    private String street;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 100)
    private String zip;

    private String description;

    @Size(max = 200)
    private String slogan;
    
    @Size(max = 200)
    private String slug;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }
    
    

    public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	@Override
    public String toString() {
        return "OrganizationForm [name=" + name + ", phone=" + phone + ", fax="
                + fax + ", website=" + website + ", street=" + street
                + ", city=" + city + ", state=" + state + ", zip=" + zip + "]";
    }
}
