package com.vantage.sportsregistration.dto;

import com.vantage.sportsregistration.validation.PasswordNotEqual;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Bazlur Rahman Rokon
 * @since 6/30/15.
 */
@PasswordNotEqual(passwordFieldName = "newPassword", passwordConfirmedFieldName = "newPasswordConfirmed")
public class PasswordForm {
    @NotEmpty
    private String currentPassword;

    @NotNull
    @Size(min = 6, max = 32)
    private String newPassword;

    @NotNull
    @Size(min = 6, max = 32)
    private String newPasswordConfirmed;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

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
}
