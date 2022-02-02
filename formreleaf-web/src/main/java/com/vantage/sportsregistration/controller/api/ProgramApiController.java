package com.vantage.sportsregistration.controller.api;


import com.formreleaf.common.forms.Field;
import com.formreleaf.common.forms.Form;
import com.formreleaf.common.forms.FormProcessor;
import com.formreleaf.common.utils.StringUtils;
import com.formreleaf.common.utils.ValidationUtils;
import com.formreleaf.domain.*;
import com.formreleaf.domain.enums.RegistrationApproval;
import com.formreleaf.domain.enums.RegistrationStatus;
import com.vantage.sportsregistration.dto.FormTemplate;
import com.vantage.sportsregistration.dto.RegistrationDTO;
import com.vantage.sportsregistration.dto.RegistrationForm;
import com.vantage.sportsregistration.service.ProgramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 4/28/15.
 */

@RestController
@RequestMapping("api/v1/program/")
public class ProgramApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramApiController.class);

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

    @Autowired
    private ProgramService programService;

    @Autowired
    private FormProcessor formProcessor;

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    //<editor-fold desc="Agreements">

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(RegistrationStatus.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(RegistrationStatus.valueOf(text.toUpperCase()));
            }
        });

        binder.registerCustomEditor(RegistrationApproval.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(RegistrationApproval.valueOf(text.toUpperCase()));
            }
        });
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{programId}/agreements", method = RequestMethod.GET)
    public Set<Agreement> findAllAgreements(@PathVariable Long programId) {

        return programService.findAllAgreementByProgramId(programId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{programId}/agreement", method = RequestMethod.POST)
    public Set<Agreement> saveAgreement(@PathVariable Long programId, @RequestBody Agreement agreement) {

        return programService.addAgreementToProgram(programId, agreement);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{programId}/agreement/{agreementId}", method = RequestMethod.DELETE)
    public void removeAgreementFromProgram(@PathVariable Long programId, @PathVariable Long agreementId) {

        programService.removeAgreementFromProgram(programId, agreementId);
    }

    //</editor-fold>

    //<editor-fold desc="Policy">
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("{programId}/policies")
    public Set<Policy> getPolicies(@PathVariable Long programId) {

        return programService.findById(programId).getPolicies();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{programId}/policy", method = RequestMethod.POST)
    public Set<Policy> savePolicy(@PathVariable Long programId, @RequestBody Policy policy) {

        return programService.addPolicyToProgram(programId, policy);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{programId}/policy/{policyId}", method = RequestMethod.DELETE)
    public void removePolicyFromProgram(@PathVariable Long programId, @PathVariable Long policyId) {

        programService.removePolicyFromProgram(programId, policyId);
    }
    //</editor-fold>

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{programId}/formTemplate", method = RequestMethod.GET)
    public ResponseEntity<String> getParticipantFormTemplate(@PathVariable Long programId) {
        String formTemplate = programService.findFormTemplateByProgramId(programId);

        if (formTemplate != null && formTemplate.length() > 0) {
            return new ResponseEntity<>(formTemplate, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "formTemplate", method = RequestMethod.POST)
    public ResponseEntity<?> saveParticipantData(@Valid @RequestBody FormTemplate formTemplate, BindingResult result) {

        try {
            validateFormTemplate(formTemplate, result);
        } catch (Exception e) {
            LOGGER.error("Unable to process form", e);
            String errorMessage = messageSourceAccessor.getMessage("FormTemplate.unableToProcess");

            return new ResponseEntity<>(new ErrorMessage(errorMessage), HttpStatus.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            List<String> errors = result
                    .getAllErrors()
                    .stream()
                    .map(objectError -> messageSourceAccessor.getMessage(objectError.getCode()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(new ErrorMessage(errors), HttpStatus.BAD_REQUEST);
        }

        programService.updateProgram(formTemplate);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String getProgram(@PathVariable Long id) {

        return programService.findById(id).getFormTemplate();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "{programId}/location", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Location addLocationToProgram(@PathVariable Long programId, @Valid @RequestBody Location location) {

        return programService.saveLocationOfProgram(programId, location);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "{programId}/location", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Location updateLocationOfProgram(@PathVariable Long programId, @Valid @RequestBody Location location) {

        return programService.saveLocationOfProgram(programId, location);
    }

    @RequestMapping(value = "{programId}/locations", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Location> findAllLocationsOfProgram(@PathVariable Long programId) {

        return programService.findLocationByProgramId(programId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "{programId}/location/{locationId}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteLocation(@PathVariable Long programId, @PathVariable Long locationId) {

        programService.deleteLocationById(programId, locationId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "{programId}/contact", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Contact addContactToProgram(@PathVariable Long programId, @Valid @RequestBody Contact contact) {

        return programService.saveContactOfProgram(programId, contact);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "{programId}/contact", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Contact updateContactOfProgram(@PathVariable Long programId, @Valid @RequestBody Contact contact) {

        return programService.saveContactOfProgram(programId, contact);
    }

    @RequestMapping(value = "{programId}/contacts", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Set<Contact> findAllContactsOfProgram(@PathVariable Long programId) {

        return programService.findAllContactsOfProgram(programId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "{programId}/contact/{contactId}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteContact(@PathVariable Long programId, @PathVariable Long contactId) {

        programService.deleteContactById(programId, contactId);
    }

    @RequestMapping(value = "public/{programId}/sections", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<Section> findAllSections(@PathVariable Long programId) {

        return programService.findAllSectionsOfProgram(programId)
                .stream()
                .sorted((s1, s2) -> s1.getName().compareTo(s1.getName()))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "{programId}/section", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Section saveSection(@PathVariable Long programId, @Valid @RequestBody Section section) {

        return programService.saveSectionOfProgram(programId, section);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "{programId}/section", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Section editSection(@PathVariable Long programId, @Valid @RequestBody Section section) {

        return programService.saveSectionOfProgram(programId, section);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "{programId}/section/delete/{sectionId}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteSection(@PathVariable Long programId, @PathVariable Long sectionId) {
        programService.deleteSectionById(programId, sectionId);
    }

    @RequestMapping(value = "registration", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Registration submitRegistration(@RequestBody RegistrationForm registration) {
        LOGGER.info("submitRegistration() registration.getSignatures().size(): {}", registration.getSignatures().size());

        return programService.saveRegistration(registration);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{programId}/publish", method = RequestMethod.GET)
    public Publish findPublish(@PathVariable Long programId) {

        return programService.findPublishByProgramId(programId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{programId}/publish", method = RequestMethod.POST)
    public ResponseEntity<?> savePublish(@PathVariable Long programId, @Valid @RequestBody Publish publish) {
        Program program = programService.findById(programId);

        List<String> errors = new ArrayList<>();

        validatePublish(program, errors);
        LOGGER.info("errors.size(): {}", errors.size());

        if (errors.size() > 0) {

            return new ResponseEntity<>(new ErrorMessage(errors), HttpStatus.BAD_REQUEST);
        }

        Publish publishSaved = programService.savePublish(programId, publish);

        return new ResponseEntity<>(publishSaved, HttpStatus.OK);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{programId}/registrants", method = RequestMethod.GET)
    public List<RegistrationDTO> viewRegistrants(@PathVariable Long programId) {

        List<Registration> registrations = programService.findAllRegistrationByProgram(programId);

        return convert(registrations);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{programId}/registrants/change-status")
    public List<RegistrationDTO> changeStatus(@PathVariable Long programId, @RequestBody List<RegistrationDTO> registrationDTOs) {

        return convert(programService.changeStatus(programId, registrationDTOs));
    }

    private List<RegistrationDTO> convert(List<Registration> registrations) {

        return registrations.stream().map(registration -> {
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

            return registrationDTO;
        }).collect(Collectors.toList());
    }

    private void validatePublish(Program program, List<String> errors) {
        String formTemplate = program.getFormTemplate();

        if (StringUtils.isEmpty(formTemplate)) {
            errors.add(messageSourceAccessor.getMessage("NotEmpty.program.fromTemplate"));
        }

        if (program.getContacts().size() == 0) {
            errors.add(messageSourceAccessor.getMessage("NotEmpty.program.contacts"));
        }

        if (program.getLocations().size() == 0) {
            errors.add(messageSourceAccessor.getMessage("NotEmpty.program.locations"));
        }
//TODO section can be empty
//        if (program.getSections().size() == 0) {
//            errors.add(messageSourceAccessor.getMessage("NotEmpty.program.sections"));
//        }

        if (program.getAgreements().size() == 0) {
            errors.add(messageSourceAccessor.getMessage("NotEmpty.program.agreement"));
        }
    }

    //Check weather user selected any fields, its required to select at least one fields to proceed,
    // this method filter out all fields from the form object tree and check weather there is any selected fields
    private void validateFormTemplate(FormTemplate formTemplate, BindingResult bindingResult) {
        Optional<Form> processedForm = formProcessor.process(formTemplate.getTemplate());
        	
        if (processedForm.isPresent()) {
            Form form = processedForm.get();

            Set<Field> selectedFields = form.getSections()
                    .stream()
                    .map(section -> section.getBlocks()
                            .stream()
                            .map(block -> block.getFields()
                                    .stream()
                                    .filter(Field::getSelected)
                                    .collect(Collectors.toSet()))
                            .collect(Collectors.toSet()))
                    .collect(Collectors.toSet()).stream()
                    .flatMap((Set<Set<Field>> sets) -> sets
                            .stream()
                            .flatMap(fields -> fields
                                    .stream()
                                    .map(field -> field)))
                    .collect(Collectors.<Field>toSet());

            if (selectedFields.size() < 1) {
                ValidationUtils.addFieldError("formTemplate", "formTemplate", formTemplate.getTemplate(), "NotEmpty.formTemplate.template", bindingResult);
            }
        }
    }
}
