package com.vantage.sportsregistration.dto;

import com.vantage.sportsregistration.validation.PasswordNotEqual;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Md. Asaduzzaman
 * @since 8/27/15.
 */
@PasswordNotEqual(passwordFieldName = "newPassword", passwordConfirmedFieldName = "newPasswordConfirmed")
public class PasswordChangeForm {

    @NotNull
    @Size(min = 6, max = 32)
    private String newPassword;

    @NotNull
    @Size(min = 6, max = 32)
    private String newPasswordConfirmed;
    
    private String passwordResetToken;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirmed() {
        return newPasswordConfirmed;
    }

    public void setNewPasswordConfirmed(String newPasswordConfirmed) {
        this.newPasswordConfirmed = newPasswordConfirmed;
    }

	public String getPasswordResetToken() {
		return passwordResetToken;
	}

	public void setPasswordResetToken(String passwordResetToken) {
		this.passwordResetToken = passwordResetToken;
	}
    
    
}
