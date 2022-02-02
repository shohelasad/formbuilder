package com.vantage.sportsregistration.controller;


import com.formreleaf.domain.Registration;
import com.formreleaf.domain.enums.RegistrationStatus;
import com.vantage.sportsregistration.service.RegistrationService;
import com.vantage.sportsregistration.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Bazlur Rahman Rokon
 * @since 4/13/15.
 */

@Controller
@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_APPLICATION')")
public class RegistrationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "registration/confirmation", method = RequestMethod.GET)
    public String showRegistrationConfirmation(Model uiModel, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Your registration has been completed successfully");

        return "redirect:/registration/list";
    }

    @RequestMapping(value = "registration/list", method = RequestMethod.GET)
    public String showRegistrationHistory(Model uiModel, RedirectAttributes redirectAttributes) {
        Set<Registration> registrations = registrationService.findAllRegistrationOfCurrentUser();
        uiModel.addAttribute("registrations", getRegistration(registrations, registration -> registration.getRegistrationStatus() == RegistrationStatus.COMPLETED));
        uiModel.addAttribute("registrationsUncompleted", getRegistration(registrations, registration -> registration.getRegistrationStatus() == RegistrationStatus.INCOMPLETE));
        uiModel.addAttribute("registrationsCanceled", getRegistration(registrations, registration -> registration.getRegistrationStatus() == RegistrationStatus.CANCELED));

        redirectAttributes.addFlashAttribute("message", "Your registration has been completed successfully");
        
        userService.findByEmail("shohel.asad@gmail.com");

        return "registration/registrationHistory";
    }

    private List<Registration> getRegistration(Set<Registration> registrations, final Predicate<Registration> registrationFilterPredicate) {

        return registrations
                .stream()
                .filter(registrationFilterPredicate)
                .sorted((left, right) -> right.getRegistrationDate().compareTo(left.getRegistrationDate()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "registration/view/{id}", method = RequestMethod.GET)
    public String viewSingleRegistrant(@PathVariable Long id, Model uiModel) {
        Registration registration = registrationService.findRegistrationById(id);
        checkOwnerShip(registration);
        uiModel.addAttribute("registration", registration);

        return "registration/single-registrant";
    }

    private void checkOwnerShip(Registration registration) {
        List<Long> registrationIds = registrationService.findAllRegistrationIdsOfCurrentLoggedInUser();

        if (!userService.isCurrentLoggedInUserIsOrganization() && !registrationIds.stream().anyMatch(aLong -> aLong.equals(registration.getId()))) {
            throw new AccessDeniedException("You don't have sufficient permission to access this resource");
        }
    }
}
