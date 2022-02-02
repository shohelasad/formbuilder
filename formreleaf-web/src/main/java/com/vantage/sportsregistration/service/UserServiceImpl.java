package com.vantage.sportsregistration.service;

import com.formreleaf.common.utils.StringUtils;
import com.formreleaf.domain.PasswordResetToken;
import com.formreleaf.domain.Profile;
import com.formreleaf.domain.Role;
import com.formreleaf.domain.User;
import com.formreleaf.domain.enums.ProfileDataType;
import com.formreleaf.repository.PasswordResetTokenRepository;
import com.formreleaf.repository.ProfileRepository;
import com.formreleaf.repository.UserRepository;
import com.vantage.sportsregistration.dto.UserForm;
import com.vantage.sportsregistration.exceptions.ResourceNotFoundException;
import com.vantage.sportsregistration.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String FIRST_PARENT_OR_GUARDIANS_FIRST_NAME = "firstParentOrGuardian_firstParentOrGuardian_firstName";
    private static final String FIRST_PARENT_OR_GUARDIANS_LAST_NAME = "firstParentOrGuardian_firstParentOrGuardian_lastName";
    private static final String PARTICIPANT_INFORMATION_PERSONAL_FIRST_NAME = "participantInformation_personal_firstName";
    private static final String PARTICIPANT_INFORMATION_PERSONAL_LAST_NAME = "participantInformation_personal_lastName";
    private static final String ADDRESS_HOME_ADDRESS_ADDRESS = "address_homeAddress_address";
    private static final String INSURANCE_INSURANCE_CARRIER = "insurance_insurance_carrier";
    private static final String PHYSICIANS_PRIMARY_CARE_PHYSICIAN_FIRST_NAME = "physicians_primaryCarePhysician_firstName";
    private static final String PHYSICIANS_PRIMARY_CARE_PHYSICIAN_LAST_NAME = "physicians_primaryCarePhysician_lastName";
    private static final String EMERGENCY_CONTACTS_FIRST_EMERGENCY_CONTACT_FIRST_NAME = "emergencyContacts_firstEmergencyContact_firstName";
    private static final String EMERGENCY_CONTACTS_FIRST_EMERGENCY_CONTACT_LAST_NAME = "emergencyContacts_firstEmergencyContact_lastName";
    private static final String CONCERNS_AND_COMMENTS_ACADEMIC_BEHAVIORAL_ACADEMIC = "concernsAndComments_academicBehavioral_academic";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

    @PersistenceContext
    private EntityManager em;

    @Cacheable(value="userCache", key="#email", unless="#result = null")
    @Override
    public User findByEmail(String email) {
    	
    	System.out.println("caching test email...");
    	slowQuery(10000L);
        System.out.println("Find from database by email...");
    	
        return userRepository.findByEmailIgnoreCaseAndEnabledTrue(email);
    }
    
    @Cacheable(value="userCache", key="#email2", unless="#result == null")
    @Override
    public User findByEmail2(String email) {
    	
    	System.out.println("caching test email2...");
    	slowQuery(10000L);
        System.out.println("Find from database by email2...");
    	
        return userRepository.findByEmailIgnoreCaseAndEnabledTrue(email);
    }
    
    private void slowQuery(long seconds) {
	    try {
                Thread.sleep(seconds);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
	}

    @Override
    public Page<User> findAll(Pageable pageable) {

        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> findAllParentUser(Pageable pageable) {

        return userRepository.findAllParentUserEnabledTrue(pageable);
    }

    @Override
    public Page<User> findAllParentUser(Pageable pageable, String q) {
        if (StringUtils.isEmpty(q)) {

            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        } else {
            String searchString = StringUtils.getTrimmedString(q);

            return userRepository.findAllParentUserEnabledTrue(pageable, searchString.toLowerCase());
        }
    }

    @Override
    public Optional<User> findById(Long id) {

        return userRepository.findOneByIdAndEnabledTrue(id);
    }

    @Override
    public User updateUser(UserForm userForm) {

        return findById(userForm.getId())
                .map(user -> {
                    user.setFirstName(userForm.getFirstName());
                    user.setLastName(userForm.getLastName());
                    return userRepository.save(user);
                }).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void remove(Long id) {
        userRepository.delete(id);
    }

   
    @Override
    public Optional<User> findCurrentLoggedInUser() {  	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
    		throw new UserNotFoundException("User is not authenticated");
    	}
    	
    	User principal = (User) authentication.getPrincipal();
        LOGGER.info("[event: FIND_CURRENT_LOGIN] - principal: {}", principal);
        //slowQuery(10000L);
        System.out.println("Find from database...");
        return userRepository.findOneByIdAndEnabledTrueAndLockedFalse(principal.getId());
    }

    //@Cacheable(value="authCache", condition="#result != null")
    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return !(authentication == null || authentication instanceof AnonymousAuthenticationToken);
    }
    
	@Override
	public boolean isParentUser() {
		return findCurrentLoggedInUser()
				.map(user -> user.getUserRoles().stream().anyMatch(userRole -> userRole.getRole() == Role.ROLE_USER))
				.orElse(false);
	}

    @Override
    public Set<Profile> findParticipantsByType(ProfileDataType profileDataType) {

        return findCurrentLoggedInUser()
                .map(loggedInUser -> profileRepository.findAllByUserAndProfileDataType(loggedInUser, profileDataType.getName()))
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Set<Profile> findParticipantsByNameAndType(String participantName, ProfileDataType profileDataType) {

        return findCurrentLoggedInUser()
                .map(loggedInUser -> profileRepository.findAllByNameAndProfileDataType(participantName, profileDataType.getName()))
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public String findParticipant(Long participantId) {

        return profileRepository.findOne(participantId).getProfileData();
    }

    public String findProfileName(Profile profile, String name) {

        Query query = em.createNativeQuery("select 0 as id, data->'value' as name, '' as profileDataType, data as profileData from "
                + "(select jsonb_array_elements(jsonb_array_elements(p->'blocks')->'fields') as data from "
                + "jsonb_array_elements( '[" + profile.getProfileData() + "]'" + ") as p) as q where q.data @> '{\"name\" : \"" + name + "\"}' ", "profile");

        LOGGER.info("query : {}", query.toString());

        query.getFirstResult();

        if (query.getResultList().size() > 0) {
            profile = (Profile) query.getResultList().get(0);

            return profile.getName().replaceAll("\"", "");
        }

        return "";
    }

    public String findProfileComment(Profile profile, String comment) {

        Query query = em.createNativeQuery("select 0 as id, data->'value' as name, '' as profileDataType, data as profileData from "
                + "(select jsonb_array_elements(jsonb_array_elements(p->'blocks')->'fields') as data from "
                + "jsonb_array_elements( '[" + profile.getProfileData() + "]'" + ") as p) q where q.data @> '{\"comment\" : \"" + comment + "\"}' ", "profile");

        if (query.getResultList().size() > 0) {
            profile = (Profile) query.getResultList().get(0);

            return profile.getName().replaceAll("\"", "");
        }

        return "";
    }

    @Override
    public Set<Profile> saveParticipant(Profile profile) {

        return findCurrentLoggedInUser()
                .map(loggedInUser -> {
                    Optional.ofNullable(profile.getId())
                            .map(id -> updateParticipantProfile(profile, id))
                            .orElseGet(() -> saveNewParticipantProfile(profile, loggedInUser));

                    return profileRepository.findAllByUserAndProfileDataType(loggedInUser, ProfileDataType.PARTICIPANT.getName());
                }).orElseThrow(UserNotFoundException::new);
    }

    private Profile saveNewParticipantProfile(Profile profile, User loggedInUser) {
        profile.setName(getParticipantProfileName(profile));
        profile.setProfileDataType(ProfileDataType.PARTICIPANT.getName());
        profile.setUser(loggedInUser);

        return profileRepository.save(profile);
    }

    private Profile updateParticipantProfile(Profile profile, Long id) {

        return profileRepository.findOneById(id)
                .map(profileFromDb -> {
                    profile.setProfileData(profile.getProfileData());
                    profile.setName(getParticipantProfileName(profile));

                    return profileRepository.save(profile);
                }).orElseThrow(() -> new ResourceNotFoundException("Profile not found by id " + id));
    }

    private String getParticipantProfileName(Profile profile) {

        return (String.format("%s %s", findProfileName(profile, PARTICIPANT_INFORMATION_PERSONAL_FIRST_NAME),
                findProfileName(profile, PARTICIPANT_INFORMATION_PERSONAL_LAST_NAME)));
    }

    @Override
    public Set<Profile> deleteProfile(String profile, Long profileId) {
        profileRepository.delete(profileId);

        return findCurrentLoggedInUser()
                .map(loggedInUser -> profileRepository.findAllByUserAndProfileDataType(loggedInUser, profile))
                .orElseThrow(UserNotFoundException::new);
    }
    
    //@Cacheable(value="userCache", key="#userRole", condition="#result != null")
    @Override
    public boolean isCurrentLoggedInUserIsOrganization() {

        return findCurrentLoggedInUser()
                .map(loggedInUser -> loggedInUser.getUserRoles()
                        .stream()
                        .allMatch(userRole -> userRole.getRole().equals(Role.ROLE_ADMIN)))
                .orElse(Boolean.FALSE);
    }
    
    //TODO: check duplicate method
   /* @Override
    public boolean isCurrentUserIsOrganizationUser() {

        return findCurrentLoggedInUser().map(currentLoggedInUser -> currentLoggedInUser.getUserRoles()
                .stream()
                .anyMatch(userRole -> (userRole.getRole() == Role.ROLE_ADMIN)
                        || (userRole.getRole() == Role.ROLE_APPLICATION))).orElse(false);
    }*/

    @Override
    public void changePassword(String newPassword) {
        findCurrentLoggedInUser().ifPresent(user -> {
            user.setSalt(StringUtils.generateRandomString(16));
            user.setHashedPassword(messageDigestPasswordEncoder.encodePassword(newPassword, user.getSalt()));
            userRepository.save(user);
        });
    }

    @Override
    public void changeUserPassword(User user, String newPassword) {
        user.setSalt(StringUtils.generateRandomString(16));
        user.setHashedPassword(messageDigestPasswordEncoder.encodePassword(newPassword, user.getSalt()));
        userRepository.save(user);
    }

    @Override
    public Set<Profile> saveParent(Profile profile) {

        return findCurrentLoggedInUser().map(loggedInUser -> {
            Optional.ofNullable(profile.getId())
                    .map(id -> saveParentProfile(profile, id))
                    .orElseGet(() -> updateParentProfile(profile, loggedInUser));

            return profileRepository.findAllByUserAndProfileDataType(loggedInUser, ProfileDataType.PARENT.getName());
        }).orElseThrow(UserNotFoundException::new);
    }

    private Profile updateParentProfile(Profile profile, User loggedInUser) {
        profile.setName(getParentProfileName(profile));
        profile.setProfileDataType(ProfileDataType.PARENT.getName());
        profile.setUser(loggedInUser);

        return profileRepository.save(profile);
    }

    private Profile saveParentProfile(Profile profile, Long id) {

        return profileRepository.findOneById(id)
                .map(profileFromDb -> {
                    profileFromDb.setProfileData(profile.getProfileData());
                    profileFromDb.setName(getParentProfileName(profile));
                    return profileRepository.save(profileFromDb);
                }).orElseThrow(() -> new ResourceNotFoundException("Profile not found by id " + id));
    }

    private String getParentProfileName(Profile profile) {

        return String.format("%s %s", findProfileName(profile, FIRST_PARENT_OR_GUARDIANS_FIRST_NAME),
                findProfileName(profile, FIRST_PARENT_OR_GUARDIANS_LAST_NAME));
    }

    @Override
    public Set<Profile> saveAddress(Profile profile) {

        return findCurrentLoggedInUser()
                .map(loggedInUser -> {
                    Optional.ofNullable(profile.getId())
                            .map(id -> updateAddressProfile(profile, id))
                            .orElseGet(() -> saveAddressProfile(profile, loggedInUser));

                    return profileRepository.findAllByUserAndProfileDataType(loggedInUser, ProfileDataType.ADDRESS.getName());
                }).orElseThrow(UserNotFoundException::new);
    }

    private Profile saveAddressProfile(Profile profile, User loggedInUser) {
        profile.setName(findProfileName(profile, ADDRESS_HOME_ADDRESS_ADDRESS));
        profile.setProfileDataType(ProfileDataType.ADDRESS.getName());
        profile.setUser(loggedInUser);

        return profileRepository.save(profile);
    }

    private Profile updateAddressProfile(Profile profile, Long id) {

        return profileRepository.findOneById(id)
                .map(profileFromDb -> {
                    profileFromDb.setProfileData(profile.getProfileData());
                    profileFromDb.setName(findProfileName(profile, ADDRESS_HOME_ADDRESS_ADDRESS));

                    return profileRepository.save(profileFromDb);
                }).orElseThrow(() -> new ResourceNotFoundException("Profile not found by id" + id));
    }

    @Override
    public Set<Profile> saveInsurance(Profile profile) {

        return findCurrentLoggedInUser()
                .map(userLoggedIn -> {
                    Optional.ofNullable(profile.getId())
                            .map(id -> updateInsuranceProfile(profile, id))
                            .orElseGet(() -> saveInsuranceProfile(profile, userLoggedIn));

                    return profileRepository.findAllByUserAndProfileDataType(userLoggedIn, ProfileDataType.INSURANCE.getName());
                }).orElseThrow(UserNotFoundException::new);
    }

    private Profile updateInsuranceProfile(Profile profile, Long id) {

        return profileRepository.findOneById(id)
                .map(profileFromDb -> {
                    profileFromDb.setName(findInsuranceProfileName(profile));
                    profileFromDb.setProfileData(profile.getProfileData());

                    return profileRepository.save(profileFromDb);
                }).orElseThrow(ResourceNotFoundException::new);
    }

    private Profile saveInsuranceProfile(Profile profile, User loggedInUser) {
        profile.setName(findInsuranceProfileName(profile));
        profile.setProfileDataType(ProfileDataType.INSURANCE.getName());
        profile.setUser(loggedInUser);

        return profileRepository.save(profile);
    }

    public String findInsuranceProfileName(Profile profile) {

        return findProfileName(profile, INSURANCE_INSURANCE_CARRIER);
    }

    @Override
    public Set<Profile> saveMedication(Profile profile) {

        return findCurrentLoggedInUser().map(userLoggedIn -> {
            Optional.ofNullable(profile.getId())
                    .map(id -> updateMedicationProfile(profile, id))
                    .orElseGet(() -> saveMedicationProfile(profile, userLoggedIn));

            return profileRepository.findAllByUserAndProfileDataType(userLoggedIn, ProfileDataType.MEDICATION.getName());
        }).orElseThrow(UserNotFoundException::new);
    }

    private Profile updateMedicationProfile(Profile profile, Long id) {

        return profileRepository.findOneById(id)
                .map(profileFromDb -> {
                    profileFromDb.setProfileData(profile.getProfileData());

                    return profileRepository.save(profileFromDb);
                }).orElseThrow(() -> new ResourceNotFoundException("Profile not found by id" + id));
    }

    private Profile saveMedicationProfile(Profile profile, User userLoggedIn) {
        profile.setProfileDataType(ProfileDataType.MEDICATION.getName());
        profile.setUser(userLoggedIn);

        return profileRepository.save(profile);
    }

    @Override
    public Set<Profile> savePhysician(Profile profile) {

        return findCurrentLoggedInUser().map(loggedInUser -> {
            Optional.ofNullable(profile.getId())
                    .map(id -> updatePhysicianProfile(profile, id))
                    .orElseGet(() -> savePhysicianProfile(profile, loggedInUser));

            return profileRepository.findAllByUserAndProfileDataType(loggedInUser, ProfileDataType.PHYSICIAN.getName());
        }).orElseThrow(UserNotFoundException::new);
    }

    private Profile savePhysicianProfile(Profile profile, User loggedInUser) {
        profile.setName(findPhysicianProfileName(profile));
        profile.setProfileDataType(ProfileDataType.PHYSICIAN.getName());
        profile.setUser(loggedInUser);

        return profileRepository.save(profile);
    }

    private Profile updatePhysicianProfile(Profile profile, Long id) {

        return profileRepository.findOneById(id)
                .map(profileFromDb -> {
                    profileFromDb.setProfileData(profile.getProfileData());
                    profileFromDb.setName(findPhysicianProfileName(profile));
                    return profileRepository.save(profileFromDb);
                }).orElseThrow(() -> new ResourceNotFoundException("Profile not found by id" + id));
    }

    private String findPhysicianProfileName(Profile profile) {

        return String.format("%s %s", findProfileName(profile, PHYSICIANS_PRIMARY_CARE_PHYSICIAN_FIRST_NAME),
                findProfileName(profile, PHYSICIANS_PRIMARY_CARE_PHYSICIAN_LAST_NAME));
    }

    @Override
    public Set<Profile> saveContact(Profile profile) {

        return findCurrentLoggedInUser().map(userLoggedIn -> {

            Optional.ofNullable(profile.getId())
                    .map(id -> updateContactProfile(profile))
                    .orElseGet(() -> saveContactProfile(profile, userLoggedIn));

            return profileRepository.findAllByUserAndProfileDataType(userLoggedIn, ProfileDataType.CONTACT.getName());
        }).orElseThrow(UserNotFoundException::new);
    }

    private Profile saveContactProfile(Profile profile, User userLoggedIn) {
        profile.setName(findContactProfileName(profile));
        profile.setProfileDataType(ProfileDataType.CONTACT.getName());
        profile.setUser(userLoggedIn);

        return profileRepository.save(profile);
    }

    private Profile updateContactProfile(Profile profile) {

        return profileRepository.findOneById(profile.getId())
                .map(profileFromDb -> {
                    profileFromDb.setProfileData(profile.getProfileData());
                    profileFromDb.setName(findContactProfileName(profile));

                    return profileRepository.save(profileFromDb);
                }).orElseThrow(() -> new ResourceNotFoundException("Profile not found by id " + profile.getId()));
    }

    private String findContactProfileName(Profile profile) {

        return String.format("%s %s", findProfileName(profile, EMERGENCY_CONTACTS_FIRST_EMERGENCY_CONTACT_FIRST_NAME),
                findProfileName(profile, EMERGENCY_CONTACTS_FIRST_EMERGENCY_CONTACT_LAST_NAME));
    }

    @Override
    public Set<Profile> saveConcern(Profile profile) {

        return findCurrentLoggedInUser().map(userLoggedIn -> {
            Optional.ofNullable(profile.getId())
                    .map(id -> updateConcernProfile(profile))
                    .orElseGet(() -> saveConcernProfile(profile, userLoggedIn));

            return profileRepository.findAllByUserAndProfileDataType(userLoggedIn, ProfileDataType.CONCERN.getName());
        }).orElseThrow(UserNotFoundException::new);
    }


    private Profile saveConcernProfile(Profile profile, User userLoggedIn) {
        profile.setProfileDataType(ProfileDataType.CONCERN.getName());
        profile.setUser(userLoggedIn);

        return profileRepository.save(profile);
    }

    private Profile updateConcernProfile(Profile profile) {

        return profileRepository.findOneById(profile.getId())
                .map(profileFromDb -> {
                    profileFromDb.setProfileData(profile.getProfileData());

                    return profileRepository.save(profileFromDb);
                }).orElseThrow(() -> new ResourceNotFoundException(" Profile not found by id " + profile.getId()));
    }

    private String findConcernProfileName(Profile profile) {

        return findProfileComment(profile, CONCERNS_AND_COMMENTS_ACADEMIC_BEHAVIORAL_ACADEMIC);
    }

    @Override
    public void lock(Long id) {
        findById(id)
                .map(user -> {
                    user.setLocked(true);

                    return userRepository.save(user);
                }).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void unlock(Long id) {
        findById(id)
                .map(user -> {
                    user.setLocked(false);

                    return userRepository.save(user);
                }).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public User getUserByPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token).getUser();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCaseAndEnabledTrue(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found by " + username);
        }

        return user;
    }

}

