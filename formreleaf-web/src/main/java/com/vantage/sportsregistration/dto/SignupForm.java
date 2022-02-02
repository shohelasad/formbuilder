package com.vantage.sportsregistration.dto;

import com.vantage.sportsregistration.validation.PasswordNotEqual;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/4/15.
 */
@PasswordNotEqual(passwordFieldName = "password", passwordConfirmedFieldName = "passwordConfirmed")
public class SignupForm extends UserForm {
    public static final String FIELD_NAME_EMAIL = "email";
    public static final String FIELD_NAME_ORGANIZATION_NAME = "organizationName";
    public static final String FIELD_NAME_PHONE_NUMBER = "phoneNumber";

    @NotEmpty
    @Size(min = 6, max = 32)
    @NotNull
    private String password;

    @NotEmpty
    @Size(min = 6, max = 32)
    private String passwordConfirmed;

    private Boolean agreedTerms;

    private boolean isOrganization;

    @Size(max = 120)
    private String organizationName;
    
    @Size(max = 120)
    private String slug;

    //@Pattern(regexp = "^$|(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$")
    private String websiteUrl;

    //TODO fix this pattern
    //@Pattern(regexp = "^$|[+]?[01]?[- .]?(\\\\([2-9]\\\\d{2}\\\\)|[2-9]\\\\d{2})[- .]?\\\\d{3}[- .]?\\\\d{4}$")
    private String phoneNumber;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmed() {
        return passwordConfirmed;
    }

    public void setPasswordConfirmed(String passwordConfirmed) {
        this.passwordConfirmed = passwordConfirmed;
    }

    public boolean getIsOrganization() {
        return isOrganization;
    }

    public void setIsOrganization(boolean isOrganization) {
        this.isOrganization = isOrganization;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public Boolean getAgreedTerms() {
        return agreedTerms;
    }

    public void setAgreedTerms(Boolean agreedTerms) {
        this.agreedTerms = agreedTerms;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
    
    
}
