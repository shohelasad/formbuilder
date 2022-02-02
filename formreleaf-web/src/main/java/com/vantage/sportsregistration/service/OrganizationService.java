package com.vantage.sportsregistration.service;

import com.formreleaf.domain.*;
import com.vantage.sportsregistration.dto.OrganizationForm;
import com.vantage.sportsregistration.dto.SignupForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;


public interface OrganizationService {

    Optional<Organization> findBySlug(String slug);

    Optional<Organization> findByName(String name);

    boolean isNameExist(String name, Organization organization);

    void remove(long id);

    Organization saveOrganization(OrganizationForm organization);

    Page<Organization> findAll(Pageable pageable, String q);

    Agreement updateOrganizationAgreements(Agreement agreement);

    Set<Agreement> findAllAgreements();

    void deleteAgreement(Long id);

    Policy saveOrganizationPolicy(Policy policy);

    Set<Policy> findAllPolicies();

    void deletePolicy(Long id);

    Set<Program> findPublishedProgramByOrganization(Organization organization);

    void addNewUserToOrganization(SignupForm signupForm);

    Set<User> findAllUsers();

	Page<Organization> findAll(Pageable pageable);

	void delete(Long id);

	Set<User> findAllUsers(String slug);

	void addNewUserToOrganizationBySlug(SignupForm signupForm);

    void indexOrganization();
}
