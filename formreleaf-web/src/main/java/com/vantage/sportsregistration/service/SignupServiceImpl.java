package com.vantage.sportsregistration.service;

import com.formreleaf.common.utils.StringUtils;
import com.formreleaf.domain.Organization;
import com.formreleaf.domain.Role;
import com.formreleaf.domain.User;
import com.formreleaf.domain.UserRole;
import com.formreleaf.repository.OrganizationRepository;
import com.formreleaf.repository.UserRepository;
import com.vantage.sportsregistration.dto.SignupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/4/15.
 */

@Service
public class SignupServiceImpl implements SignupService {

    @Autowired
    private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public User createNewUser(SignupForm signupForm) {
        User user = new User();
        user.setEmail(signupForm.getEmail());
        user.setFirstName(signupForm.getFirstName());
        user.setLastName(signupForm.getLastName());
        user.setEnabled(true);
        user.setLocked(false);
        user.setSalt(StringUtils.generateRandomString(16));
        user.setHashedPassword(messageDigestPasswordEncoder.encodePassword(signupForm.getPassword(), user.getSalt()));

        if (signupForm.getIsOrganization()) {
            Organization organization = new Organization();
            organization.setName(signupForm.getOrganizationName());
            organization.setSlug(StringUtils.slugify(signupForm.getOrganizationName()));
            organization.setPhone(signupForm.getPhoneNumber());
            organization.setWebsite(signupForm.getWebsiteUrl());
            user.setOrganization(organization);
            user.setUserRoles(Collections.singletonList(new UserRole(user, Role.ROLE_ADMIN)).stream().collect(Collectors.toSet()));
        } else {
            user.setUserRoles(Collections.singletonList(new UserRole(user, Role.ROLE_USER)).stream().collect(Collectors.toSet()));
        }

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {

        return userRepository.findByEmailIgnoreCaseAndEnabledTrue(email);
    }

    @Override
    public Optional<Organization> findOrganizationByName(String organizationName) {

        return organizationRepository.findByName(organizationName);
    }
}
