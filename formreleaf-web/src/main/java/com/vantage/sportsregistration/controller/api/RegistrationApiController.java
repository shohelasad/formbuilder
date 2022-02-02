package com.vantage.sportsregistration.controller.api;

import com.formreleaf.domain.Profile;
import com.formreleaf.domain.Registration;
import com.formreleaf.domain.Section;
import com.formreleaf.domain.Signature;
import com.vantage.sportsregistration.dto.RegistrationDTO;
import com.vantage.sportsregistration.dto.RegistrationForm;
import com.vantage.sportsregistration.exceptions.ResourceNotFoundException;
import com.vantage.sportsregistration.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Bazlur Rahman Rokon
 * @date 5/17/15.
 */
@Controller
@RequestMapping(value = "/api/v1/registration")
public class RegistrationApiController {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

    @Autowired
    private RegistrationService registrationService;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("{registrationId}")
    public Registration findRegistration(@PathVariable Long registrationId) {

        return registrationService.findRegistrationById(registrationId);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("{registrationId}/formData")
    public String findFormData(@PathVariable Long registrationId) {

        return registrationService.findFormDataById(registrationId);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("{registrationId}/sections")
    public List<Section> findAllSection(@PathVariable Long registrationId) {

        return registrationService.findAllSections(registrationId)
                .stream()
                .sorted((s1, s2) -> s1.getName().compareTo(s2.getName()))
                .collect(Collectors.toList());
    }

    private Supplier<ResourceNotFoundException> resourceNotFoundException() {
        return () -> new ResourceNotFoundException("Registered section may be deleted or not active");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("{registrationId}/signature")
    public Set<Signature> findSignature(@PathVariable Long registrationId) {

        return registrationService.findSignatures(registrationId);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Registration submitRegistration(@RequestBody RegistrationForm registration) {

        return registrationService.saveRegistration(registration);
    }

    @RequestMapping(value = "auto-save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> autoSave(@RequestBody RegistrationForm registrationForm) {

        final Registration registration = registrationService.saveIncompleteRegistration(registrationForm);
        final RegistrationDTO registrationDTO = convert(registration);

        return ResponseEntity.ok(registrationDTO);
    }

    @RequestMapping(value = "create/profile", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Profile> saveRegistrantProfile(@RequestBody Long registrationId) {

        return registrationService.saveProfileByRegistration(registrationId);
    }

    @RequestMapping(value = "history", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Registration> findAllRegistrationOfCurrentUser() {

        return registrationService.findAllRegistrationOfCurrentUser();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{registrationId}/change-section", method = RequestMethod.POST)
    public RegistrationDTO changeSection(@PathVariable Long registrationId, @RequestBody Long sectionId) {

        return convert(registrationService.changeSection(registrationId, sectionId));
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "delete/{registrationId}", method = RequestMethod.DELETE)
    public void deleteRegistration(@PathVariable Long registrationId){

        registrationService.deleteRegistration(registrationId);
    }


    private RegistrationDTO convert(Registration registration) {

        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setId(registration.getId());
        registrationDTO.setRegistrantName(registration.getRegistrantName());
        registrationDTO.setOrganizationName(registration.getProgram().getOrganization().getName());
        registrationDTO.setOrganizationSlug(registration.getProgram().getOrganization().getSlug());
        registrationDTO.setProgramName(registration.getProgram().getName());
        registrationDTO.setProgramSlug(registration.getProgram().getSlug());
        registrationDTO.setRegistrationApproval(registration.getRegistrationApproval());
        registrationDTO.setRegistrationStatus(registration.getRegistrationStatus());
        registrationDTO.setSectionNames(registration.getSectionNames());
        registrationDTO.setRegistrationDate(dateFormat.format(registration.getRegistrationDate()));
        registrationDTO.setLastModifiedDate(registration.getLastModifiedDate());

        return registrationDTO;
    }
}
