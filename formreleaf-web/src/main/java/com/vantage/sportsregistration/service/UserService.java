package com.vantage.sportsregistration.service;

import com.formreleaf.domain.PasswordResetToken;
import com.formreleaf.domain.Profile;
import com.formreleaf.domain.User;
import com.formreleaf.domain.enums.ProfileDataType;
import com.vantage.sportsregistration.dto.UserForm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;
import java.util.Set;

public interface UserService extends UserDetailsService {

	User findByEmail(String email);
	
	User findByEmail2(String email);

	Page<User> findAll(Pageable pageable);

	Optional<User> findById(Long id);

	User updateUser(UserForm user);

	void remove(Long id);

	Optional<User> findCurrentLoggedInUser();

	boolean isAuthenticated();

	String findParticipant(Long participantId);

	Set<Profile> saveParticipant(Profile profile);

	Set<Profile> saveAddress(Profile profile);

	Set<Profile> saveInsurance(Profile profile);

	Set<Profile> saveMedication(Profile profile);

	Set<Profile> savePhysician(Profile profile);

	Set<Profile> saveConcern(Profile profile);

	Set<Profile> saveContact(Profile profile);

	Set<Profile> saveParent(Profile profile);

	Set<Profile> findParticipantsByType(ProfileDataType profileDataType);

	Set<Profile> findParticipantsByNameAndType(String participantName, ProfileDataType profileDataType);

	Set<Profile> deleteProfile(String profile, Long profileId);

	boolean isCurrentLoggedInUserIsOrganization();

	void changePassword(String newPassword);

	void lock(Long id);

	void unlock(Long id);

	Page<User> findAllParentUser(Pageable pageable);

	Page<User> findAllParentUser(Pageable pageable, String q);

	//boolean isCurrentUserIsOrganizationUser();

	void createPasswordResetTokenForUser(User user, String token);

	PasswordResetToken getPasswordResetToken(String token);

	User getUserByPasswordResetToken(String token);

	void changeUserPassword(User user, String newPassword);

	boolean isParentUser();

}
