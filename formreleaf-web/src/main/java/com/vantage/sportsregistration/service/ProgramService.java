package com.vantage.sportsregistration.service;

import com.formreleaf.domain.*;
import com.vantage.sportsregistration.dto.FormTemplate;
import com.vantage.sportsregistration.dto.RegistrationDTO;
import com.vantage.sportsregistration.dto.RegistrationForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;


public interface ProgramService {

    List<Program> findByName(String name);

    Program findById(Long id);

    Program updateProgram(FormTemplate program);

    Page<Program> findAll(Pageable pageable);

    void delete(Long id);

    Program saveProgramOverview(Program program);

    String findFormTemplateByProgramId(Long id);

    void updateProgramPublish(Program program);

    void deleteContactById(Long programId, Long contactId);

    Set<Contact> findAllContactsOfProgram(Long programId);

    Set<Section> findAllSectionsOfProgram(Long programId);

    void deleteSectionById(Long programId, Long sectionId);

    Section saveSectionOfProgram(Long programId, Section section);

    Location saveLocationOfProgram(Long programId, Location program);

    Set<Location> findLocationByProgramId(Long programId);

    void deleteLocationById(Long programId, Long locationId);

    Set<Agreement> addAgreementToProgram(Long programId, Agreement agreement);

    Set<Policy> addPolicyToProgram(Long programId, Policy policy);

    void removePolicyFromProgram(Long programId, Long policyId);

    void removeAgreementFromProgram(Long programId, Long agreementId);

    Program findBySlug(String slug);

    Registration saveRegistration(RegistrationForm registration);

    Set<Registration> findAllRegistration();

    Contact saveContactOfProgram(Long programId, Contact contact);

    Registration findRegistrationById(Long registrationId);

    Publish findPublishByProgramId(Long programId);

    Program loadProgramById(Long programId);

    Publish savePublish(Long programId, Publish publish);

    Page<Registration> findAllRegistrationByProgram(Long programId, Pageable pageable);

    List<Registration> findAllRegistrationByProgram(Long programId);

    List<Registration> changeStatus(Long programId, List<RegistrationDTO> registrationDTOs);

    Set<Registration> findAllByRegistrantName(String name);

    Set<Agreement> findAllAgreementByProgramId(Long programId);

    Program findBySlug(String organizationSlug, String programSlug);

    List<Long> findAllProgramIdsOfCurrentLoggedInOrganization();

	Program cloneNewProgram(Program program);

	List<Program> findBySimilarName(String name);
}
