package com.vantage.sportsregistration.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/5/15.
 * <p>
 */
public class UserForm {
	
	private long id;
	
    @Email
    @NotEmpty
    @Size(max = 100)
    private String email;

    @NotEmpty
    @Size(max = 100)
    private String firstName;

    @NotEmpty
    @Size(max = 100)
    private String lastName;

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
