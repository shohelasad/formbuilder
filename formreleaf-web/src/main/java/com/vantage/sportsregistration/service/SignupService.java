package com.vantage.sportsregistration.service;

import com.formreleaf.domain.Organization;
import com.formreleaf.domain.User;
import com.vantage.sportsregistration.dto.SignupForm;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/4/15.
 */

@Component
public interface SignupService {
    User createNewUser(SignupForm signupForm);

    User findByEmail(String email);

    Optional<Organization> findOrganizationByName(String organizationName);
}
