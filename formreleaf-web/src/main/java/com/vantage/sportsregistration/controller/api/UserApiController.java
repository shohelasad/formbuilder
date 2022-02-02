package com.vantage.sportsregistration.controller.api;

import com.formreleaf.domain.Profile;
import com.formreleaf.domain.enums.ProfileDataType;
import com.vantage.sportsregistration.service.UserService;
import com.formreleaf.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.util.Set;


@RestController
@RequestMapping("api/v1/user")
public class UserApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    private UserService userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ProfileDataType.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                String capitalized = text.toUpperCase();
                ProfileDataType profileDataType = ProfileDataType.valueOf(capitalized);
                setValue(profileDataType);
            }
        });
    }

    @RequestMapping(value = "profile/{profileDataType}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> getProfileData(@PathVariable(value = "profileDataType") ProfileDataType profileDataType) {
        LOGGER.info("getProfileData()-> profileDataType ={}", profileDataType);

        return userService.findParticipantsByType(profileDataType);
    }

    @RequestMapping(value = "participant/{participantName}/profile/{profileDataType}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> getParticipantProfileData(@PathVariable(value = "participantName") String participantName, @PathVariable(value = "profileDataType") ProfileDataType profileDataType) {
        LOGGER.info("getParticipantProfileData()-> profileDataType ={}", profileDataType + " ParticipantName: " + participantName);

        return userService.findParticipantsByNameAndType(participantName, profileDataType);
    }

    @RequestMapping(value = "subParticipant/{participantName}/profile/{profileDataType}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> getParticipantProfileDataWithType(@PathVariable(value = "participantName") String participantName, @PathVariable(value = "profileDataType") ProfileDataType profileDataType) {
        LOGGER.info("getParticipantProfileDataWithType()-> profileDataType ={}", profileDataType + " ParticipantName: " + participantName);

        Set<Profile> profiles = userService.findParticipantsByNameAndType(participantName, profileDataType);
        for (Profile profile : profiles) {
            if (StringUtils.isEmpty(profile.getProfileName())) {
                profile.setName(profile.getName());
            } else {
                profile.setName(profile.getProfileName());
            }
        }
        return profiles;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "participant/{participantId}", method = RequestMethod.GET)
    public ResponseEntity<String> getParticipant(@PathVariable Long participantId) {

        String participantForm = userService.findParticipant(participantId);

        if (participantForm != null && participantForm.length() > 0) {
            return new ResponseEntity<>(participantForm, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{profile}/{profileId}/delete", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> deleteProfile(@PathVariable String profile, @PathVariable Long profileId) {

        return userService.deleteProfile(profile, profileId);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "participant/save", method = RequestMethod.POST)
    public Set<Profile> saveParticipant(@Valid @RequestBody Profile profile, BindingResult result) {

        return userService.saveParticipant(profile);
    }

    @RequestMapping(value = "/address/save", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> saveAddress(@Valid @RequestBody Profile profile) {

        return userService.saveAddress(profile);
    }

    @RequestMapping(value = "/contact/save", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> saveContact(@Valid @RequestBody Profile profile) {

        return userService.saveContact(profile);
    }

    @RequestMapping(value = "/parent/save", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> saveParent(@Valid @RequestBody Profile profile) {

        return userService.saveParent(profile);
    }

    @RequestMapping(value = "/insurance/save", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> saveInsurance(@Valid @RequestBody Profile profile) {

        return userService.saveInsurance(profile);
    }

    @RequestMapping(value = "/medication/save", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> saveMedication(@Valid @RequestBody Profile profile) {

        return userService.saveMedication(profile);
    }

    @RequestMapping(value = "/physician/save", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> savePhysician(@Valid @RequestBody Profile profile) {

        return userService.savePhysician(profile);
    }

    @RequestMapping(value = "/concern/save", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> saveConcern(@Valid @RequestBody Profile profile) {

        return userService.saveConcern(profile);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "is-organization", method = RequestMethod.GET)
    public boolean isLoggedInUserIsOrganization() {

        return userService.isCurrentLoggedInUserIsOrganization();
    }
    
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "public/is-authenticated", method = RequestMethod.GET)
    public boolean isUserIsAuthenticated() {

        return userService.isAuthenticated();
    }
}
