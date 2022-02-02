package com.vantage.sportsregistration.service;


import com.formreleaf.common.utils.DateUtils;
import com.formreleaf.common.utils.StringUtils;
import com.formreleaf.domain.*;
import com.formreleaf.domain.enums.ProgramStatus;
import com.formreleaf.domain.enums.PublishStatus;
import com.formreleaf.domain.enums.RegistrationStatus;
import com.formreleaf.repository.*;
import com.vantage.sportsregistration.dto.FormTemplate;
import com.vantage.sportsregistration.dto.RegistrationDTO;
import com.vantage.sportsregistration.dto.RegistrationForm;
import com.vantage.sportsregistration.exceptions.ResourceNotFoundException;
import com.vantage.sportsregistration.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Service
@Transactional
public class ProgramServiceImpl implements ProgramService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramServiceImpl.class);

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private AgreementRepository agreementRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private SignatureRepository signatureRepository;

    @Override
    public List<Program> findByName(String name) {

        return programRepository.findByName(name);
    }
    
    @Override
    public List<Program> findBySimilarName(String name) {

        return programRepository.findBySimilarName(name);
    }

    private void addSlug(Program program) {
        String slug = StringUtils.slugify(program.getName());
        List<Program> programs = findByName(program.getName());
        
        if (programs != null && !programs.stream().anyMatch(item -> item.getId().equals(program.getId()))) {
            slug += UUID.randomUUID().toString(); 
        }
        program.setSlug(slug);
    }
   

    @Override
    public Program findById(Long id) {

        return programRepository.findOneById(id)
                .map(program -> program)
                .orElseThrow(programNotFound(id));
    }

    @Override
    public void delete(Long id) {
        programRepository.delete(id);
    }

    @Override
    public Program saveProgramOverview(Program program) {

        return Optional.ofNullable(program.getId())
                .map(id -> updateProgramOverview(program))
                .orElseGet(() -> userService.findCurrentLoggedInUser()
                        .map(userLoggedIn -> saveNewProgramOverview(program, userLoggedIn))
                        .orElseThrow(UserNotFoundException::new));
    }

    private Program updateProgramOverview(Program program) {

        return programRepository.findOneById(program.getId())
                .map(programFromDb -> {
                    programFromDb.setName(program.getName());
                    programFromDb.setTag(program.getTag());
                    programFromDb.setDescription(program.getDescription());
                    addSlug(programFromDb);

                    return programRepository.save(programFromDb);
                }).orElseThrow(() -> new ResourceNotFoundException("Program not found by id " + program.getId()));
    }

    private Program saveNewProgramOverview(Program program, User currentLoggedInUser) {
        Publish publish = new Publish();
        publish.setProgramStatus(ProgramStatus.DRAFT);
        publish.setPublishStatus(PublishStatus.PRIVATE);
        program.setPublish(publish);
        addSlug(program);
        program.setOrganization(currentLoggedInUser.getOrganization());

        return programRepository.save(program);
    }
    
    @Override
    public Program cloneNewProgram(Program program) {	
    	Program cloneProgram = new Program();
        //set program overview
    	addCloneName(program, cloneProgram);
    	cloneProgram.setDescription(program.getDescription());
    	cloneProgram.setTag(program.getTag());
    	cloneProgram.setImage(program.getImage());
    	addSlug(cloneProgram);
        cloneProgram.setOrganization(program.getOrganization());
    	
    	return createCloneProgram(program, cloneProgram);
    }
    
    private void addCloneName(Program program, Program cloneProgram) {
    	String name = program.getName();
    	if(name.contains("(copy")) {
    		name = name.substring(0, name.lastIndexOf("(copy"));
    	}
        
    	List<Program> programs = findBySimilarName(name);
        if (programs != null) {
            name += "(copy " + programs.size() +")"; 
        }
        else {
        	name += "(copy 1)"; 
        }
        
        cloneProgram.setName(name);
    }
    
    private Program createCloneProgram(Program program, Program cloneProgram)
    {
    	//set program contacts
    	Set<Contact> contacts = cloneProgram.getContacts();
    	contacts.clear();
    	contacts.addAll(program.getContacts().stream().map(c-> { 
    		 		Contact contact = new Contact();			
                	BeanUtils.copyProperties(c, contact, "id","program"); 
                	contact.setProgram(cloneProgram);
                	
                	return contact;
    		}).collect(Collectors.toSet()));
    	cloneProgram.setContacts(contacts);
    	
    	//set program location
    	Set<Location> locations = cloneProgram.getLocations();
    	locations.clear();
    	locations.addAll(program.getLocations().stream().map(l-> { 
    		 		Location location = new Location();			
                	BeanUtils.copyProperties(l, location, "id", "program"); 
                	location.setProgram(cloneProgram);
                	
                	return location;
    		}).collect(Collectors.toSet()));
    	cloneProgram.setLocations(locations);
    	
    	//set program section
    	Set<Section> sections = cloneProgram.getSections();
    	sections.clear();
    	sections.addAll(program.getSections().stream().map(s-> { 
		 		Section section = new Section();			
	        	BeanUtils.copyProperties(s, section, "id", "program"); 
	        	section.setProgram(cloneProgram);
	        	
	        	return section;
			}).collect(Collectors.toSet()));
    	cloneProgram.setSections(sections);
    
    	//set program agreement
    	Set<Agreement> agreements = cloneProgram.getAgreements();
    	agreements.clear();
    	agreements.addAll(program.getAgreements());
    	cloneProgram.setAgreements(agreements);
    	
    	//set program policy    	
    	Set<Policy> policies = cloneProgram.getPolicies();
    	policies.clear();
    	policies.addAll(program.getPolicies());
	    cloneProgram.setPolicies(policies);
    	
    	//set form template/participant
    	cloneProgram.setFormTemplate(program.getFormTemplate());
    	
    	//set publish to default
    	Publish publish = new Publish();
        publish.setProgramStatus(ProgramStatus.DRAFT);
        publish.setPublishStatus(PublishStatus.PRIVATE);
        cloneProgram.setPublish(publish);
    	 
    	return programRepository.save(cloneProgram);
    }

    @Override
    public String findFormTemplateByProgramId(Long id) {

        return programRepository.findFormTemplateByProgramId(id);
    }

    @Override
    public void updateProgramPublish(Program program) {
        Program programFromDb = programRepository.findOne(program.getId());

        programFromDb.setPublish(program.getPublish());
        programFromDb.getPublish().setPublishStatus(PublishStatus.PUBLIC);

        programRepository.save(programFromDb);
    }

    @Override
    public Location saveLocationOfProgram(Long programId, Location location) {

        return Optional.ofNullable(location.getId())
                .map(id -> updateLocation(location))
                .orElseGet(() -> programRepository.findOneById(programId).map(program -> {
                    location.setProgram(program);

                    return locationRepository.save(location);
                }).orElseThrow(programNotFound(programId)));
    }

    private Location updateLocation(Location location) {

        return locationRepository.findOneById(location.getId())
                .map(locationFromDb -> {
                    locationFromDb.setName(location.getName());
                    locationFromDb.setAddressLine1(location.getAddressLine1());
                    locationFromDb.setAddressLine2(location.getAddressLine2());
                    locationFromDb.setCity(location.getCity());
                    locationFromDb.setCountry(location.getCountry());
                    locationFromDb.setState(location.getState());
                    locationFromDb.setZip(location.getZip());

                    return locationRepository.save(locationFromDb);
                }).orElseThrow(() -> new ResourceNotFoundException("Location not found by id ", location.getId()));
    }

    @Override
    public Set<Location> findLocationByProgramId(Long programId) {

        return programRepository.findOneById(programId)
                .map(Program::getLocations)
                .orElseThrow(programNotFound(programId));
    }

    @Override
    public void deleteLocationById(Long programId, Long locationId) {
        programRepository.findOneById(programId)
                .ifPresent(program -> program.getLocations().removeIf(location -> location.getId().equals(locationId)));
    }

    @Override
    public Contact saveContactOfProgram(Long programId, Contact contact) {

        return Optional.ofNullable(contact.getId())
                .map(id -> updateContact(contact))
                .orElseGet(() -> programRepository.findOneById(programId).map(program -> {
                    contact.setProgram(program);

                    return contactRepository.save(contact);
                }).orElseThrow(programNotFound(programId)));
    }

    private Contact updateContact(Contact contact) {
        return contactRepository.findOneById(contact.getId()).map(contactFromDb -> {
            contactFromDb.setFirstName(contact.getFirstName());
            contactFromDb.setLastName(contact.getLastName());
            contactFromDb.setTitle(contact.getTitle());
            contactFromDb.setEmail(contact.getEmail());
            contactFromDb.setWorkPhone(contact.getWorkPhone());
            contactFromDb.setCellPhone(contact.getCellPhone());

            return contactRepository.save(contactFromDb);
        }).orElseThrow(() -> new ResourceNotFoundException("Contact not found by id", contact.getId()));
    }

    @Override
    public List<Registration> changeStatus(Long programId, List<RegistrationDTO> registrationDTOs) {

        registrationDTOs.forEach(registrationDTO -> registrationRepository.findOneById(registrationDTO.getId())
                .ifPresent(registration -> {

                    LOGGER.info("registration status: {}, approval : {}", registrationDTO.getRegistrationStatus(), registrationDTO.getRegistrationApproval());

                    Optional.ofNullable(registrationDTO.getRegistrationApproval())
                            .ifPresent(approval -> registration.setRegistrationApproval(registrationDTO.getRegistrationApproval()));

                    Optional.ofNullable(registrationDTO.getRegistrationStatus())
                            .ifPresent(status -> registration.setRegistrationStatus(registrationDTO.getRegistrationStatus()));

                    registrationRepository.save(registration);
                }));


        return registrationRepository.findAllByDeletedFalseAndProgram_IdOrderByRegistrantNameAsc(programId);
    }

    private TreeSet<Registration> getSortedRegistration(Set<Registration> allRegistrations) {
        return allRegistrations
                .stream()
                .sorted((r1, r2) -> r1.getRegistrant().compareTo(r2.getRegistrant()))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public Set<Registration> findAllByRegistrantName(String name) {
        Optional<User> currentLoggedInUser = userService.findCurrentLoggedInUser();

        return currentLoggedInUser.map(user -> {

            Organization organization = user.getOrganization();
            Set<Program> programs = organization.getPrograms();
            List<Long> ids = programs.stream().mapToLong(Program::getId).boxed().collect(Collectors.toList());


            if (ids != null && ids.size() > 0) {
                return registrationRepository.findAllByRegistrantNameContainingAndDeletedFalseAndProgram_IdIn(name, ids);
            } else {
                return Collections.<Registration>emptySet();// return empty
            }

        }).orElseGet(HashSet::new);
    }

    @Override
    public Set<Agreement> findAllAgreementByProgramId(Long programId) {
        Optional<Program> program = programRepository.findOneById(programId);

        return program.map(Program::getAgreements).orElse(Collections.emptySet());
    }

    @Override
    public Program loadProgramById(Long programId) {
        return programRepository.findOneById(programId).map(program -> {
            program.getContacts();
            program.getLocations();
            program.getSections();

            return program;
        }).orElseThrow(programNotFound(programId));
    }

    @Override
    public Publish findPublishByProgramId(Long programId) {
        Program program = programRepository.findOne(programId);

        return program.getPublish();
    }

    @Override
    public Publish savePublish(Long programId, Publish publish) {
        
    	Program program = programRepository.findOne(programId);
        Date today = DateUtils.getDateWithoutTime();

        if (publish.getRegistrationOpenDate() != null && publish.getRegistrationCloseDate() != null) {
        	if (!publish.isRegistrationOpened() || today.after(publish.getRegistrationCloseDate())) {
                Publish publishFromDb = program.getPublish();
        		setPublishStatus(publishFromDb, publish);
                publishFromDb.setProgramStatus(ProgramStatus.CLOSED);            
            } 
        	else if (today.before(publish.getRegistrationOpenDate())){
                Publish publishFromDb = program.getPublish();
        		setPublishStatus(publishFromDb, publish);
                publishFromDb.setProgramStatus(ProgramStatus.PENDING);
            }
        	else {
        		Publish publishFromDb = program.getPublish();
        		setPublishStatus(publishFromDb, publish);
                publishFromDb.setProgramStatus(ProgramStatus.OPEN); 		
        	}
        }

        if (publish.getPublishStartDate() != null && publish.getPublishEndDate() != null) {
        	if(!publish.isProgramOpened() || today.before(publish.getPublishStartDate()) || today.after(publish.getPublishEndDate())) {
                Publish publishFromDb = program.getPublish();
        		setPublishStatus(publishFromDb, publish);
        		publishFromDb.setPublishStatus(PublishStatus.PRIVATE);
        	}
        	else { 	
                Publish publishFromDb = program.getPublish();
        		setPublishStatus(publishFromDb, publish);
                publishFromDb.setPublishStatus(PublishStatus.PUBLIC);
            }  	
        }

        return programRepository.save(program).getPublish();
    }
    
    private Publish setPublishStatus(Publish publishFromDb, Publish publish) {
    	publishFromDb.setPublishStartDate(publish.getPublishStartDate());
        publishFromDb.setPublishEndDate(publish.getPublishEndDate());
        publishFromDb.setRegistrationCloseDate(publish.getRegistrationCloseDate());
        publishFromDb.setRegistrationOpenDate(publish.getRegistrationOpenDate());
        publishFromDb.setRegistrationOpened(publish.isRegistrationOpened());
        publishFromDb.setProgramOpened(publish.isProgramOpened());
        
    	return publishFromDb;
    }

    @Override
    public Page<Registration> findAllRegistrationByProgram(Long programId, Pageable pageable) {

        return registrationRepository.findAllByProgram_IdAndRegistrationStatusAndDeletedFalse(programId, RegistrationStatus.COMPLETED, pageable);
    }

    @Override
    public List<Registration> findAllRegistrationByProgram(Long programId) {

        return registrationRepository.findAllByProgram_IdAndRegistrationStatusAndDeletedFalseOrderByLastNameAsc(programId, RegistrationStatus.COMPLETED);
    }

    @Override
    public void deleteContactById(Long programId, Long contactId) {
        Program program = programRepository.findOne(programId);
        program.getContacts().removeIf(contact -> contact.getId().equals(contactId));

        programRepository.save(program);
    }

    @Override
    public Set<Contact> findAllContactsOfProgram(Long programId) {

        return programRepository.findOneById(programId)
                .map(Program::getContacts).
                        orElse(Collections.emptySet());
    }

    @Override
    public Set<Agreement> addAgreementToProgram(Long programId, Agreement agreement) {

        return programRepository.findOneById(programId)
                .map(program -> addAgreementToProgram(program, agreement))
                .orElseThrow(programNotFound(programId))
                .getAgreements()
                .stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .collect(Collectors.<Agreement>toSet());
    }

    private Program addAgreementToProgram(Program program, Agreement agreement) {
        return agreementRepository.findOneById(agreement.getId()).map(agreementToAdd -> {
            program.getAgreements().add(agreementToAdd);
            return programRepository.save(program);
        }).orElseThrow(() -> new ResourceNotFoundException("Agreement not found in database by", agreement.getId()));
    }

    @Override
    public Set<Policy> addPolicyToProgram(Long programId, Policy policy) {

        return programRepository.findOneById(programId)
                .map(program -> addPolicyToProgram(program, policy))
                .orElseThrow(programNotFound(programId))
                .getPolicies()
                .stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .collect(Collectors.toSet());
    }

    private Program addPolicyToProgram(Program program, Policy policy) {
        return policyRepository.findOneById(policy.getId()).map(policyToAdd -> {
            program.getPolicies().add(policyToAdd);

            return programRepository.save(program);
        }).orElseThrow(() -> new ResourceNotFoundException("Policy not found by id " + policy.getId()));
    }


    @Override
    public void removePolicyFromProgram(Long programId, Long policyId) {
        programRepository.findOneById(programId)
                .map(program -> {
                    program.getPolicies()
                            .removeIf(policy -> policy.getId().equals(policyId));
                    return programRepository.save(program);
                }).orElseThrow(programNotFound(programId));
    }

    @Override
    public void removeAgreementFromProgram(Long programId, Long agreementId) {
        programRepository.findOneById(programId)
                .map(program -> {
                    program.getAgreements()
                            .removeIf(agreement -> agreement.getId().equals(agreementId));
                    return programRepository.save(program);
                }).orElseThrow(programNotFound(programId));
    }

    @Override
    public Program findBySlug(String slug) {
        return programRepository.findBySlug(slug).map(program -> {
            program.getOrganization();

            return program;
        }).orElseThrow(programNotFound(slug));
    }

    @Override
    public Registration saveRegistration(RegistrationForm registrationForm) {

        return Optional.ofNullable(registrationForm.getId())
                .map(id -> updateRegistration(registrationForm))
                .orElseGet(() -> saveNewRegistration(registrationForm));
    }

    private Registration saveNewRegistration(RegistrationForm registrationForm) {

        return userService.findCurrentLoggedInUser()
                .map(loggedInUser -> saveNewRegistration(registrationForm, loggedInUser))
                .orElseThrow(UserNotFoundException::new);
    }

    private Registration saveNewRegistration(RegistrationForm registrationForm, User loggedInUser) {

        return programRepository.findOneById(registrationForm.getProgramId()).map(program -> {
            Registration registration = new Registration();
            registration.setProgram(program);
            Set<Section> sections = registrationForm.getSectionIds()
                    .stream().map(sectionRepository::findOneById)
                    .filter(Optional::isPresent).map(Optional::get)
                    .collect(Collectors.toSet());
            registration.setSections(sections);
            registration.setRegistrationDate(new Date());
            registration.setFormData(registrationForm.getFormTemplate());
            registration.setRegistrant(loggedInUser);
            registration.setSignatures(registrationForm.getSignatures());

            return registrationRepository.save(registration);
        }).orElseThrow(programNotFound(registrationForm.getProgramId()));
    }

    private Registration updateRegistration(RegistrationForm registrationForm) {

       return registrationRepository.findOneById(registrationForm.getId())
                .map(registration -> {
                    Set<Section> sections = registrationForm.getSectionIds()
                            .stream()
                            .map(sectionRepository::findOneById)
                            .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
                    registration.setSections(sections);
                    registration.setFormData(registrationForm.getFormTemplate());
                    Set<Signature> signatures = registrationForm.getSignatures().stream()
                            .map(signature -> signatureRepository.findOneById(signature.getId())
                                    .map(s -> {
                                        s.setSign(signature.getSign());
                                        return s;
                                    })).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
                    registration.setSignatures(signatures);

                    final Registration savedRegistration = registrationRepository.save(registration);

                    return savedRegistration;
                }).<ResourceNotFoundException>orElseThrow(() -> new ResourceNotFoundException("Registration not found by id ", registrationForm.getId()));
    }

    @Override
    public Set<Registration> findAllRegistration() {
        return userService.findCurrentLoggedInUser()
                .map(userLoggedIn -> {
                    Set<Registration> registrations = registrationRepository.findAllRegistrationsByUserId(userLoggedIn.getId());
                    //dummy call to load all data
                    registrations.stream().forEach(registration -> registration.getProgram().getOrganization());

                    return registrations;
                }).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Program updateProgram(FormTemplate formTemplate) {
        final Program programFromDb = programRepository.findOne(formTemplate.getProgramId());
        String temp = formTemplate.getTemplate();
        temp = "[" + temp + "]"; //TODO: small hack, have to revisit later
        programFromDb.setFormTemplate(temp);

        return programRepository.save(programFromDb);
    }

    @Override
    public Page<Program> findAll(Pageable pageable) {
        return userService.findCurrentLoggedInUser().map(loggedInUser -> {
            Page<Program> programs = programRepository
                    .findProgramByOrganizationId(pageable, loggedInUser.getOrganization().getId());
            programs.getContent()
                    .forEach(program -> program.setRegistrationCount(registrationRepository.countByProgramAndRegistrationStatus(program, RegistrationStatus.COMPLETED)));

            return programs;
        }).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Set<Section> findAllSectionsOfProgram(Long programId) {

        return programRepository.findOneById(programId)
                .map(Program::getSections)
                .orElse(Collections.emptySet());
    }

    @Override
    public void deleteSectionById(Long programId, Long sectionId) {
        Program program = programRepository.findOne(programId);
        program.getSections().removeIf(section -> section.getId().equals(sectionId));
        programRepository.save(program);
    }

    @Override
    public Section saveSectionOfProgram(Long programId, Section section) {
        return programRepository.findOneById(programId)
                .map(program -> Optional.ofNullable(section.getId())
                        .map(id -> updateSection(section, id)).orElseGet(() -> {
                            section.setProgram(program);
                            return sectionRepository.save(section);
                        })).orElseThrow(programNotFound(programId));
    }


    private Section updateSection(Section section, Long id) {

        return sectionRepository.findOneById(section.getId()).map(sectionFromDb -> {
            sectionFromDb.setOpenDate(section.getOpenDate());
            sectionFromDb.setCloseDate(section.getCloseDate());
            sectionFromDb.setMeetingTime(section.getMeetingTime());
            sectionFromDb.setName(section.getName());
            sectionFromDb.setPrice(section.getPrice());
            sectionFromDb.setSectionCode(section.getSectionCode());
            sectionFromDb.setSpaceLimit(section.getSpaceLimit());

            return sectionRepository.save(sectionFromDb);
        }).orElseThrow(() -> new ResourceNotFoundException("Section not found by id ", id));
    }

    @Override
    public Registration findRegistrationById(Long registrationId) {
        Registration registration = registrationRepository.findOne(registrationId);
        registration.getSections(); //dummy call;
        registration.getProgram().getOrganization();

        return registration;
    }

    @Override
    public Program findBySlug(String organizationSlug, String programSlug) {
        return programRepository.findByOrganization_SlugAndSlug(organizationSlug, programSlug).map(program -> {
            program.getOrganization();

            return program;
        }).orElseThrow(programNotFound(organizationSlug + " and " + programSlug));
    }

    //TODO add second level caching
    @Override
    public List<Long> findAllProgramIdsOfCurrentLoggedInOrganization() {
        return userService.findCurrentLoggedInUser().map(user -> {
            Organization organization = user.getOrganization();

            return organization.getPrograms().stream().map(Program::getId).collect(Collectors.toList());
        }).orElseGet(Collections::emptyList);
    }

    private Supplier<ResourceNotFoundException> programNotFound(Object programId) {
        return () -> new ResourceNotFoundException("Program not found by - " + programId);
    }
}
