package com.vantage.sportsregistration.service;

import com.formreleaf.common.dto.PeopleDTO;
import com.formreleaf.domain.Profile;
import com.formreleaf.domain.Registration;
import com.formreleaf.domain.Section;
import com.formreleaf.domain.Signature;
import com.vantage.sportsregistration.dto.RegistrationForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * @author Bazlur Rahman Rokon
 * @date 5/17/15.
 */
public interface RegistrationService {

    String findFormDataById(Long registrationId);

    Set<Section> findAllSections(Long registrationId);

    Set<Registration> findAllRegistrationOfCurrentUser();

    Registration saveRegistration(RegistrationForm registration);

    Profile findParticipantFromRegistration(Long registrationId);

    Registration findRegistrationById(Long registrationId);

    Profile findAddressFromRegistration(Long registrationId);

    Profile findMedicationFromRegistration(Long registrationId);

    Profile findParentFromRegistration(Long registrationId);

    Profile findContactFromRegistration(Long registrationId);

    Profile findInsuranceFromRegistration(Long registrationId);

    Profile findPhysicianFromRegistration(Long registrationId);

    Profile findConcernFromRegistration(Long registrationId);

    Set<Profile> saveProfileByRegistration(Long registrationId);

    Set<Signature> findSignatures(Long registrationId);

    Map<PeopleDTO, Long> findAllPeople();

	Page<PeopleDTO> findAllPeople(Pageable pageable, String q);

    List<Long> findAllRegistrationIdsOfCurrentLoggedInUser();

	Page<PeopleDTO> findAllPeople(Pageable pageable);

	Registration changeSection(Long regId, Long sectionId);

    Registration saveIncompleteRegistration(RegistrationForm registration);

    Optional<Long> findCurrentEditingRegistrationId(Long programId);

    void deleteRegistration(Long id);
}
