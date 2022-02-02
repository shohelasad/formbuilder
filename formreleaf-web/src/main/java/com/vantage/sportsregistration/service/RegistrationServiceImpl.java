package com.vantage.sportsregistration.service;


import com.formreleaf.common.dto.PeopleDTO;
import com.formreleaf.common.forms.FormProcessor;
import com.formreleaf.common.utils.Constant;
import com.formreleaf.common.utils.DateUtils;
import com.formreleaf.common.utils.StringUtils;
import com.formreleaf.domain.*;
import com.formreleaf.domain.enums.ProfileDataType;
import com.formreleaf.domain.enums.RegistrationApproval;
import com.formreleaf.domain.enums.RegistrationStatus;
import com.formreleaf.repository.*;
import com.vantage.sportsregistration.dto.RegistrationForm;
import com.vantage.sportsregistration.exceptions.ResourceNotFoundException;
import com.vantage.sportsregistration.exceptions.UserNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Bazlur Rahman Rokon
 * @date 5/17/15.
 */
@Transactional
@Service
public class RegistrationServiceImpl implements RegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private FormProcessor formProcessor;

    @Autowired
    private EmailService emailService;

    @PersistenceContext
    private EntityManager em;

    @Override
    public String findFormDataById(Long registrationId) {

        return registrationRepository.findOne(registrationId).getFormData();
    }

    @Override
    public Set<Section> findAllSections(Long registrationId) {
        Registration registration = registrationRepository.findOne(registrationId);
        Set<Section> sections = registration.getProgram().getSections();

        sections.forEach(section -> {
            if (registration.getSections().contains(section)) section.setSelected(true);
        });

        return sections;
    }

    @Override
    public Set<Registration> findAllRegistrationOfCurrentUser() {
        return userService.findCurrentLoggedInUser().map(userLoggedIn -> {
            Set<Registration> registrations = registrationRepository.findAllRegistrationsByUserId(userLoggedIn.getId());
            //dummy call to load all data
            registrations.stream().forEach(registration -> {
                registration.getProgram().getOrganization();
                registration.getSections();
            });
            return registrations;
        }).orElseThrow(UserNotFoundException::new);
    }

    private void saveProfileData(Registration registration) {

        if (StringUtils.isNotEmpty(registration.getFormData())) {
            JSONObject jsonObject = new JSONObject(registration.getFormData());

            JSONArray allSections = (JSONArray) jsonObject.get("sections");

            for (int i = 0; i < allSections.length(); i++) {
                JSONObject innerObj = (JSONObject) allSections.get(i);

                String typeId = innerObj.get("id").toString();
                String profileTypeStr = "";

                switch (typeId) {
                    case Constant.PARTICIPANT:
                        saveProfileData(registration, ProfileDataType.PARTICIPANT, innerObj.toString());
                        break;
                    case Constant.ADDRESS:
                        saveProfileData(registration, ProfileDataType.ADDRESS, innerObj.toString());
                        break;
                    case Constant.PARENT:
                        saveProfileData(registration, ProfileDataType.PARENT, innerObj.toString());
                        break;
                    case Constant.SECOND_PARENT:
                        saveProfileData(registration, ProfileDataType.SECOND_PARENT, innerObj.toString());
                        break;
                    case Constant.CONTACT:
                        saveProfileData(registration, ProfileDataType.CONTACT, innerObj.toString());
                        break;
                    case Constant.PHYSICIAN:
                        saveProfileData(registration, ProfileDataType.PHYSICIAN, innerObj.toString());
                        break;
                    case Constant.INSURANCE:
                        saveProfileData(registration, ProfileDataType.INSURANCE, innerObj.toString());
                        break;
                    case Constant.MEDICATION:
                        saveProfileData(registration, ProfileDataType.MEDICATION, innerObj.toString());
                        break;
                    case Constant.CONCERN:
                        saveProfileData(registration, ProfileDataType.CONCERN, innerObj.toString());
                        break;
                }
            }
        }
    }

    private void saveProfileData(Registration registration, ProfileDataType profileDataType, String profileData) {
        final String profileStr = profileDataType.toString();

        User loggedInUser = userService.findCurrentLoggedInUser()
                .map(user -> user)
                .orElseThrow(UserNotFoundException::new);

        Set<Profile> profiles = profileRepository.findAllByUserAndNameAndProfileDataType(loggedInUser, registration.getRegistrantName().trim(), profileStr);

        Profile profile = null;

        if (profiles.size() > 0) {
            profile = (Profile) profiles.toArray()[0];
        } else {
            profile = new Profile();
            profile.setName(registration.getRegistrantName().trim());
            profile.setProfileDataType(profileStr);
            profile.setUser(loggedInUser);
            profile.setEnabled(true);
        }

        profile.setProfileData(profileData);
        profileRepository.save(profile);
    }

    private void fillRegistrantInfo(RegistrationForm registrationForm, Registration registration) {
        final PeopleDTO peopleDTO = formProcessor.getRegistrantInfo(registrationForm.getFormTemplate());
        registration.setRegistrantName(peopleDTO.getPerson());
        registration.setFirstName(peopleDTO.getFirstName());
        registration.setMiddleName(peopleDTO.getMiddleName());
        registration.setLastName(peopleDTO.getLastName());
        registration.setEmail(peopleDTO.getEmail());
        registration.setGender(peopleDTO.getGender());
        registration.setCellPhone(peopleDTO.getCellPhone());
        registration.setHomePhone(peopleDTO.getHomePhone());
        registration.setDateOfBirth(peopleDTO.getDateOfBirth());
        registration.setRegistrationDate(new Date());
    }

    @Override
    public Registration findRegistrationById(Long registrationId) {
        Registration registration = registrationRepository.findOne(registrationId);
        registration.getSections(); //dummy call;
        registration.getSignatures();
        registration.getProgram().getPolicies();
        return registration;
    }

    String findProfileName(Long registrationId, String name) {
        Query query = em.createNativeQuery("select id, r.formData->'value' as formData from"
                + "(select id, jsonb_array_elements(jsonb_array_elements(jsonb_array_elements(form_data->'sections')->'blocks')->'fields') as formData from Registration where id = ?) r "
                + "where r.formData @> '{\"name\" : \"" + name + "\"}' ", "Registration");

        query.setParameter(1, registrationId);
        Registration registration = (Registration) query.getResultList().get(0);

        return registration.getFormData().replaceAll("\"", "");
    }

    String findParticipantComment(Long registrationId, String name) {
        Query query = em.createNativeQuery("select id, r.formData->'comment' as formData from"
                + "(select id, jsonb_array_elements(jsonb_array_elements(jsonb_array_elements(form_data->'sections')->'blocks')->'fields') as formData from Registration where id = ?) r "
                + "where r.formData @> '{\"name\" : \"" + name + "\"}' ", "Registration");

        query.setParameter(1, registrationId);
        Registration registration = (Registration) query.getResultList().get(0);

        return registration.getFormData().replaceAll("\"", "");
    }

    @Override
    public Profile findParticipantFromRegistration(Long registrationId) {
        String firstName = findProfileName(registrationId, "participantInformation_personal_firstName");
        String lastName = findProfileName(registrationId, "participantInformation_personal_lastName");

        Query query = em.createNativeQuery("SELECT id, formData FROM ( "
                + "SELECT  id, jsonb_array_elements(form_data->'sections') \\:\\: JSONB AS formData FROM registration "
                + "WHERE id = ?) r "
                + "WHERE r.formData @>'{\"title\":\"Participant Information\"}' ", "Registration");

        query.setParameter(1, registrationId);
        Registration registration = (Registration) query.getResultList().get(0);
        String profileName = firstName + " " + lastName;

        Profile profile = getProfile(profileName, ProfileDataType.PARTICIPANT);
        profile.setProfileData(registration.getFormData());

        return profile;
    }

    private Profile getProfile(String profileName, ProfileDataType dataType) {
        Set<Profile> profiles = profileRepository.findAllByNameAndProfileDataType(profileName, dataType.toString());

        Profile profile = !profiles.isEmpty() && profiles.stream().findFirst().isPresent() ? profiles.stream().findFirst().get() : new Profile();
        profile.setName(profileName);
        profile.setProfileDataType(ProfileDataType.PARTICIPANT.getName());

        return profile;
    }

    @Override
    public Profile findAddressFromRegistration(Long registrationId) {
        String name = findProfileName(registrationId, "address_homeAddress_address");

        Query query = em.createNativeQuery("SELECT id, formData FROM ( "
                + "SELECT  id, jsonb_array_elements(form_data->'sections') \\:\\: JSONB AS formData FROM registration "
                + "WHERE id = ?) r "
                + "WHERE r.formData @>'{\"title\":\"Address\"}' ", "Registration");

        query.setParameter(1, registrationId);
        Registration registration = (Registration) query.getResultList().get(0);
        Profile profile = getProfile(name, ProfileDataType.ADDRESS);
        profile.setProfileData(registration.getFormData());

        return profile;
    }

    @Override
    public Profile findMedicationFromRegistration(Long registrationId) {
        String name = findProfileName(registrationId, "medications_medication1_name");

        //Registration Data
        Query query = em.createNativeQuery("SELECT id, formData FROM ( "
                + "SELECT  id, jsonb_array_elements(form_data->'sections') \\:\\: JSONB AS formData FROM registration "
                + "WHERE id = ?) r "
                + "WHERE r.formData @>'{\"title\":\"Medications\"}' ", "Registration");

        query.setParameter(1, registrationId);
        Registration registration = (Registration) query.getResultList().get(0);
        Profile profile = getProfile(name, ProfileDataType.MEDICATION);
        profile.setProfileData(registration.getFormData());

        return profile;
    }

    @Override
    public Profile findParentFromRegistration(Long registrationId) {
        String firstName = findProfileName(registrationId, "firstParentOrGuardian_firstParentOrGuardian_firstName");
        String lastName = findProfileName(registrationId, "firstParentOrGuardian_firstParentOrGuardian_lastName");

        //Registration Data
        Query query = em.createNativeQuery("SELECT id, formData FROM ( "
                + "SELECT  id, jsonb_array_elements(form_data->'sections') \\:\\: JSONB AS formData FROM registration "
                + "WHERE id = ?) r "
                + "WHERE r.formData @>'{\"title\":\"First Parent Or Guardian\"}' ", "Registration");

        query.setParameter(1, registrationId);
        Registration registration = (Registration) query.getResultList().get(0);
        String profileName = firstName + " " + lastName;
        Profile profile = getProfile(profileName, ProfileDataType.PARENT);
        profile.setProfileData(registration.getFormData());

        return profile;
    }

    @Override
    public Profile findContactFromRegistration(Long registrationId) {
        String firstName = findProfileName(registrationId, "emergencyContacts_firstEmergencyContact_firstName");
        String lastName = findProfileName(registrationId, "emergencyContacts_firstEmergencyContact_lastName");

        //Registration Data
        Query query = em.createNativeQuery("SELECT id, formData FROM ( "
                + "SELECT  id, jsonb_array_elements(form_data->'sections') \\:\\: JSONB AS formData FROM registration "
                + "WHERE id = ?) r "
                + "WHERE r.formData @>'{\"title\":\"Emergency Contacts\"}' ", "Registration");

        query.setParameter(1, registrationId);
        Registration registration = (Registration) query.getResultList().get(0);
        String profileName = firstName + " " + lastName;

        Profile profile = getProfile(profileName, ProfileDataType.CONTACT);
        profile.setProfileData(registration.getFormData());

        return profile;
    }

    @Override
    public Profile findInsuranceFromRegistration(Long registrationId) {
        String name = findProfileName(registrationId, "insurance_insurance_carrier");

        Query query = em.createNativeQuery("SELECT id, formData FROM ( "
                + "SELECT  id, jsonb_array_elements(form_data->'sections') \\:\\: JSONB AS formData FROM registration "
                + "WHERE id = ?) r "
                + "WHERE r.formData @>'{\"title\":\"Insurance\"}' ", "Registration");

        query.setParameter(1, registrationId);
        Registration registration = (Registration) query.getResultList().get(0);

        Profile profile = getProfile(name, ProfileDataType.INSURANCE);
        profile.setProfileData(registration.getFormData());

        return profile;
    }

    @Override
    public Profile findPhysicianFromRegistration(Long registrationId) {
        String firstName = findProfileName(registrationId, "physicians_primaryCarePhysician_firstName");
        String lastName = findProfileName(registrationId, "physicians_primaryCarePhysician_lastName");

        Query query = em.createNativeQuery("SELECT id, formData FROM ( "
                + "SELECT  id, jsonb_array_elements(form_data->'sections') \\:\\: JSONB AS formData FROM registration "
                + "WHERE id = ?) r "
                + "WHERE r.formData @>'{\"title\":\"Physicians\"}' ", "Registration");

        query.setParameter(1, registrationId);
        Registration registration = (Registration) query.getResultList().get(0);
        String profileName = firstName + " " + lastName;
        Profile profile = getProfile(profileName, ProfileDataType.PHYSICIAN);
        profile.setProfileData(registration.getFormData());

        return profile;
    }

    @Override
    public Profile findConcernFromRegistration(Long registrationId) {
        String name = findProfileName(registrationId, "concernsAndComments_academicBehavioral_academic");

        Query query = em.createNativeQuery("SELECT id, formData FROM ( "
                + "SELECT  id, jsonb_array_elements(form_data->'sections') \\:\\: JSONB AS formData FROM registration "
                + "WHERE id = ?) r "
                + "WHERE r.formData @>'{\"title\":\"Concerns and Comments\"}' ", "Registration");

        query.setParameter(1, registrationId);
        Registration registration = (Registration) query.getResultList().get(0);
        Profile profile = getProfile(name, ProfileDataType.CONCERN);
        profile.setProfileData(registration.getFormData());

        return profile;
    }

    @Override
    public Set<Profile> saveProfileByRegistration(Long registrationId) {
        Set<Profile> profiles = new HashSet<>();
        profiles.add(findParticipantFromRegistration(registrationId));
        profiles.add(findParentFromRegistration(registrationId));
        profiles.add(findAddressFromRegistration(registrationId));
        profiles.add(findContactFromRegistration(registrationId));
        profiles.add(findMedicationFromRegistration(registrationId));
        profiles.add(findInsuranceFromRegistration(registrationId));
        profiles.add(findPhysicianFromRegistration(registrationId));
        profiles.add(findConcernFromRegistration(registrationId));

        User currentLoggedInUser = userService.findCurrentLoggedInUser()
                .map(user -> user)
                .orElseThrow(UserNotFoundException::new);
        Set<Profile> profilesToSave = profiles
                .stream()
                .map(profile -> {
                    profile.setUser(currentLoggedInUser);

                    return profile;
                }).collect(Collectors.toSet());

        return new HashSet<>(profileRepository.save(profilesToSave));
    }

    @Override
    public Set<Signature> findSignatures(Long registrationId) {
        Registration registration = registrationRepository.findOne(registrationId);

        return registration.getSignatures()
                .stream()
                .sorted()
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public Map<PeopleDTO, Long> findAllPeople() {
        Long organizationId = findCurrentOrganizationId();

        Query query = em.createQuery("select r from Registration r, Program p, Organization o " +
                " where r.program = p.id " +
                " and p.organization = o.id " +
                " and o.id = " + organizationId +
                " and r.deleted = false");

        List<Registration> registrations = query.getResultList();

        return registrations.stream()
                .map(registration -> {
                    PeopleDTO peopleDTO = new PeopleDTO();
                    peopleDTO.setPerson(registration.getRegistrantName());
                    peopleDTO.setDateOfBirth(DateUtils.convert(registration.getDateOfBirth()));
                    peopleDTO.setGender(registration.getGender());
                    peopleDTO.setEmail(registration.getEmail());
                    peopleDTO.setHomePhone(registration.getHomePhone());
                    peopleDTO.setCellPhone(registration.getCellPhone());

                    return peopleDTO;
                }).collect(Collectors.groupingBy(o -> o, Collectors.counting()));
    }


    @Override
    public Page<PeopleDTO> findAllPeople(Pageable pageable, String q) {

        if (StringUtils.isEmpty(q)) {

            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        } else {

            String searchString = StringUtils.getTrimmedString(q);
            Long organizationId = findCurrentOrganizationId();

            Query query = em.createQuery("select count(r) from Registration r, Program p, Organization o" +
                    " where r.program = p.id" +
                    " and p.organization = o.id" +
                    " and o.id = " + organizationId +
                    " and lower(r.registrantName) like '%" + searchString.toLowerCase() + "%'" +
                    " and r.deleted = false");

            Long totalCount = (Long) query.getSingleResult();

            query = em.createQuery("select r from Registration r, Program p, Organization o" +
                    " where r.program = p.id " +
                    " and p.organization = o.id " +
                    " and o.id = " + organizationId +
                    " and lower(r.registrantName) like '%" + searchString.toLowerCase() + "%' " +
                    " and r.deleted = false order by r.lastName asc");

            List<PeopleDTO> peopleList = extractPeopleFromQuery(pageable, query);

            return new PageImpl<>(peopleList, pageable, totalCount);
        }
    }

    @Override
    public Page<PeopleDTO> findAllPeople(Pageable pageable) {
        Long organizationId = findCurrentOrganizationId();

        Query query = em.createQuery("select count(r) from Registration r, Program p, Organization o " +
                " where r.program = p.id " +
                " and p.organization = o.id " +
                " and o.id = " + organizationId + 
                " and r.deleted = false" +
                " and r.registrationStatus = 'COMPLETED'");

        Long totalCount = (Long) query.getSingleResult();

        query = em.createQuery("select r from Registration r, Program p, Organization o " +
                " where r.program = p.id " +
                " and p.organization = o.id " +
                " and o.id = " + organizationId + 
                " and r.deleted = false" +
                " and r.registrationStatus = 'COMPLETED'" + 
                " order by r.lastName asc");

        List<PeopleDTO> peopleList = extractPeopleFromQuery(pageable, query);

        return new PageImpl<>(peopleList, pageable, totalCount);
    }

    private List<PeopleDTO> extractPeopleFromQuery(Pageable pageable, Query query) {
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Registration> registrations = query.getResultList();

        return findPeoplesByRegistrantsCount(registrations);
    }

    private Long findCurrentOrganizationId() {
        return userService.findCurrentLoggedInUser()
                .map(userLoggedIn -> userLoggedIn.getOrganization().getId())
                .orElseThrow(UserNotFoundException::new);
    }

    private List<PeopleDTO> findPeoplesByRegistrantsCount(List<Registration> registrations) {
        Map<PeopleDTO, Long> peoples = registrations.stream()
                .map(registration -> {
                    PeopleDTO peopleDTO = new PeopleDTO();
                    peopleDTO.setPerson(registration.getRegistrantName());
                    peopleDTO.setFirstName(registration.getFirstName());
                    peopleDTO.setLastName(registration.getLastName());
                    peopleDTO.setMiddleName(registration.getMiddleName());
                    peopleDTO.setDateOfBirth(DateUtils.convert(registration.getDateOfBirth()));
                    peopleDTO.setGender(registration.getGender());
                    peopleDTO.setEmail(registration.getEmail());
                    peopleDTO.setHomePhone(registration.getHomePhone());
                    peopleDTO.setCellPhone(registration.getCellPhone());

                    return peopleDTO;
                }).collect(Collectors.groupingBy(o -> o, Collectors.counting()));

        return peoples.entrySet()
                .stream()
                .map(p -> {
                    PeopleDTO people = p.getKey();
                    people.setRegistrationCount(p.getValue());

                    return people;
                }).sorted((o1, o2) -> StringUtils.compareNullableString(o1.getLastName(), o2.getLastName(), true))
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findAllRegistrationIdsOfCurrentLoggedInUser() {

        return findAllRegistrationOfCurrentUser()
                .stream()
                .map(Registration::getId)
                .collect(Collectors.toList());
    }

    @Override
    public Registration changeSection(Long regId, Long sectionId) {

        return registrationRepository.findOneById(regId).map(registration -> {
            Set<Section> sections = new HashSet<>();
            sections.add(sectionRepository.getOne(sectionId));
            registration.setSections(sections);

            return registrationRepository.save(registration);
        }).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Registration saveIncompleteRegistration(RegistrationForm registrationForm) {

        return Optional.ofNullable(registrationForm.getId())
                .map(id -> updateIncompleteRegistration(registrationForm))
                .orElseGet(() -> saveNewIncompleteRegistration(registrationForm));
    }

    @Override
    public Optional<Long> findCurrentEditingRegistrationId(Long programId) {

        return userService.findCurrentLoggedInUser().map(user -> {

            final List<Registration> registrantList = registrationRepository.findAllByProgram_IdAndRegistrant_IdOrderByLastModifiedDateDesc(programId, user.getId());

            return registrantList != null && registrantList.size() > 0 ? registrantList.get(0).getId() : null;
        });
    }

    @Override
    public void deleteRegistration(Long id) {
        registrationRepository.findOneById(id)
                .ifPresent(registration -> {
                    registration.setDeleted(true);
                    registrationRepository.save(registration);
                });
    }

    private Registration updateIncompleteRegistration(RegistrationForm registrationForm) {
        Registration registration = registrationRepository.findOne(registrationForm.getId());
        registration.setSections(getSections(registrationForm.getSectionIds()));
        registration.setFormData(registrationForm.getFormTemplate());

        registration.getSignatures().forEach(signature ->
                registrationForm.getSignatures()
                        .stream()
                        .filter(newSignature -> signature.getId().equals(newSignature.getId())
                                || signature.getAgreement().getId().equals(newSignature.getAgreement().getId()))
                        .findFirst()
                        .ifPresent(s -> signature.setSign(s.getSign())));

        fillRegistrantInfo(registrationForm, registration);

        return registrationRepository.save(registration);
    }

    private Registration saveNewIncompleteRegistration(final RegistrationForm registrationForm) {

        return userService.findCurrentLoggedInUser()
                .map(user -> programRepository.findOneById(registrationForm.getProgramId())
                        .map(program -> {
                            Registration registration = new Registration();
                            registration.setProgram(program);
                            registration.setSections(getSections(registrationForm.getSectionIds()));
                            registration.setFormData(registrationForm.getFormTemplate());
                            registration.setRegistrant(user);
                            registration.setRegistrationDate(new Date());
                            registration.setSignatures(registrationForm.getSignatures());
                            registration.setRegistrationStatus(RegistrationStatus.INCOMPLETE);
                            registration.setRegistrationApproval(RegistrationApproval.NONE);
                            fillRegistrantInfo(registrationForm, registration);

                            return registrationRepository.save(registration);
                        }).<ResourceNotFoundException>orElseThrow(() -> new ResourceNotFoundException("Program not found")))
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Registration saveRegistration(RegistrationForm registrationForm) {

        final Registration registration = Optional.ofNullable(registrationForm.getId())
                .map(id -> updateRegistration(registrationForm))
                .orElseGet(() -> saveNewRegistration(registrationForm));

        fillRegistrantInfo(registrationForm, registration);
        saveProfileData(registration);

        //Async Service
        emailService.sendRegistrationNotification(registration);
        emailService.sendAdminNotification(registration);

        return registration;
    }

    private Registration updateRegistration(RegistrationForm registrationForm) {
        Registration registration = registrationRepository.findOne(registrationForm.getId());
        registration.setSections(getSections(registrationForm.getSectionIds()));
        registration.setFormData(registrationForm.getFormTemplate());
        registration.setRegistrationStatus(RegistrationStatus.COMPLETED);

        registration.getSignatures().forEach(signature ->
                registrationForm.getSignatures()
                        .stream()
                        .filter(newSignature -> signature.getId().equals(newSignature.getId())
                                || signature.getAgreement().getId().equals(newSignature.getAgreement().getId()))
                        .findFirst()
                        .ifPresent(s -> s.setSign(s.getSign())));

        return registrationRepository.save(registration);
    }

    private Registration saveNewRegistration(RegistrationForm registrationForm) {

        return userService.findCurrentLoggedInUser()
                .map(user -> programRepository.findOneById(registrationForm.getProgramId())
                        .map(programFromDb -> {
                            Registration registration = new Registration();
                            registration.setProgram(programFromDb);
                            registration.setSections(getSections(registrationForm.getSectionIds()));
                            registration.setRegistrationDate(new Date());
                            registration.setFormData(registrationForm.getFormTemplate());
                            registration.setRegistrant(user);
                            registration.setSignatures(registrationForm.getSignatures());
                            fillRegistrantInfo(registrationForm, registration);
                            registration.setRegistrationStatus(RegistrationStatus.COMPLETED);
                            registration.setRegistrationApproval(RegistrationApproval.NONE);

                            return registrationRepository.save(registration);
                        }).<ResourceNotFoundException>orElseThrow(() -> new ResourceNotFoundException("Program not found")))
                .orElseThrow(UserNotFoundException::new);
    }

    private Set<Section> getSections(List<Long> sectionIds) {

        return sectionIds
                .stream()
                .map(sectionRepository::findOne)
                .collect(Collectors.toSet());
    }
}