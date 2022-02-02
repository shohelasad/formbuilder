package com.vantage.sportsregistration.controller;


import com.formreleaf.domain.Organization;
import com.formreleaf.domain.Program;
import com.formreleaf.domain.User;
import com.vantage.sportsregistration.dto.OrganizationForm;
import com.vantage.sportsregistration.dto.SignupForm;
import com.vantage.sportsregistration.dto.UserForm;
import com.vantage.sportsregistration.exceptions.ResourceNotFoundException;
import com.vantage.sportsregistration.exceptions.UserNotFoundException;
import com.vantage.sportsregistration.service.OrganizationService;
import com.vantage.sportsregistration.service.SignupService;
import com.vantage.sportsregistration.service.UserService;
import com.formreleaf.common.utils.PageWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static com.formreleaf.common.utils.StringUtils.isEmpty;
import static com.formreleaf.common.utils.StringUtils.isNotEmpty;
import static com.formreleaf.common.utils.ValidationUtils.addFieldError;

@Controller
public class OrganizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationController.class);

    protected static final String MODEL_NAME_ORGANIZATION_FORM_DTO = "organizationForm";
    protected static final String ERROR_CODE_ORGANIZATION_NAME_NOT_EMPTY = "NotExist.organizationForm.name";
    protected static final String MODEL_NAME_SIGNUP_DTO = "signupForm";
    protected static final String ERROR_CODE_EMAIL_EXIST = "NotExist.user.email";

    protected static final String ERROR_CODE_ORGANIZATION_NAME_EXIST = "NotExist.organizationForm.name";
    protected static final String ERROR_CODE_PHONE_NUMBER_NOT_EMPTY = "NotEmpty.signupForm.phoneNumber";
    
    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private SignupService signupService;

    private Set<Program> programs;
    

    @RequestMapping(value = "/organizations/list", method = RequestMethod.GET)
    public String findOrganization(Pageable pageable, Model uiModel) {
        Page<Organization> organizationList = organizationService.findAll(pageable);
        PageWrapper<Organization> page = new PageWrapper<>(organizationList, "/organizations/list");
        uiModel.addAttribute("page", page);

        return "organization/list";
    }

    @RequestMapping(value = "/organizations/search", method = RequestMethod.GET)
    public String searchOrganization(Pageable pageable, @RequestParam(value = "q", required = false) String q, Model uiModel) {
        LOGGER.debug("searchOrganization() -> q ={}", q);
        Page<Organization> organizationList = organizationService.findAll(pageable, q.trim().toLowerCase());
        PageWrapper<Organization> page = new PageWrapper<>(organizationList, "/organizations/search?q=" + q);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("q", q);

        return "organization/search";
    }

    @RequestMapping(value = "organizations/{slug}", method = RequestMethod.GET)
    public String showProgramPublicView(@PathVariable String slug, Model uiModel) {
        Organization organization = organizationService.findBySlug(slug)
                .map(organization1 -> organization1)
                .orElseThrow(() -> new ResourceNotFoundException("No organization found by- " + slug));
        uiModel.addAttribute("organization", organization);
        Set<Program> programs = organizationService.findPublishedProgramByOrganization(organization);
        uiModel.addAttribute("programs", programs);
        uiModel.addAttribute("contacts", organization.getUsers());
        uiModel.addAttribute("isAuthenticated", userService.isAuthenticated());

        return "organization/publicView";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLICATION')")
    @RequestMapping(value = "/organization/edit", method = RequestMethod.GET)
    public String editOrganization(Model uiModel) {	
        uiModel.addAttribute("organizationForm", userService.findCurrentLoggedInUser()
                .map(user -> {
                    Organization organization = user.getOrganization();
                    OrganizationForm organizationForm = new OrganizationForm();
                    organizationForm.setId(organization.getId());
                    organizationForm.setName(organization.getName());
                    organizationForm.setPhone(organization.getPhone());
                    organizationForm.setFax(organization.getFax());
                    organizationForm.setDescription(organization.getDescription());
                    organizationForm.setWebsite(organization.getWebsite());
                    organizationForm.setSlogan(organization.getSlogan());

                    Optional.ofNullable(organization.getAddress())
                            .ifPresent(address -> {
                                organizationForm.setStreet(address.getState());
                                organizationForm.setCity(address.getCity());
                                organizationForm.setState(address.getState());
                                organizationForm.setZip(address.getZip());
                                organizationForm.setAddressLine1(address.getAddressLine1());
                                organizationForm.setAddressLine2(address.getAddressLine2());
                            });

                    return organizationForm;
                }).orElseThrow(() -> new ResourceNotFoundException("Organization not found by current logged user")));

        return "organization/edit";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLICATION')")
    @RequestMapping(value = "/organization/edit", method = RequestMethod.POST)
    public String updateOrganization(@Valid OrganizationForm organizationForm, BindingResult result,
                                     RedirectAttributes redirectAttributes) {

        validate(organizationForm, result);

        if (result.hasErrors()) {

            return "organization/edit";
        }

        Organization organization = organizationService.saveOrganization(organizationForm);

        redirectAttributes.addFlashAttribute("message", "Organization has been successfully updated");

        return "redirect:/organization/edit";
    }

    @PreAuthorize("hasRole('ROLE_APPLICATION')")
    @RequestMapping(value = "/organization/delete/{id}", method = RequestMethod.POST)
    public String deleteOrganization(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

    	organizationService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Organization has been deleted");

        return "redirect:/organization/list/";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLICATION')")
    @RequestMapping(value = "/organization/new-user", method = RequestMethod.GET)
    public String addNewUserToOrganization(SignupForm signupForm, Model uiModel) {
    	
        return "organization/newUser";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLICATION')")
    @RequestMapping(value = "/organization/new-user", method = RequestMethod.POST)
    public String saveNewUser(@Valid SignupForm signupForm, BindingResult result) {
        validate(signupForm, result);

        if (result.hasErrors()) {

            return "organization/newUser";
        }

        organizationService.addNewUserToOrganization(signupForm);

        return "redirect:/organization/user-list";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLICATION')")
    @RequestMapping(value = "/organization/user-list", method = RequestMethod.GET)
    public String listUser(Model uiModel) {
        uiModel.addAttribute("users", organizationService.findAllUsers());

        return "organization/userList";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLICATION')")
    @RequestMapping(value = "/organization/user/lock/{id}", method = RequestMethod.POST)
    public String lockUserByOrganization(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

    	userService.lock(id);
        redirectAttributes.addFlashAttribute("message", "User has been locked");

        return "redirect:/organization/user-list";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLICATION')")
    @RequestMapping(value = "/organization/user/unlock/{id}", method = RequestMethod.POST)
    public String unlockUserByOrganization(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

    	userService.unlock(id);
        redirectAttributes.addFlashAttribute("message", "User has been unocked");

        return "redirect:/organization/user-list";
    }

    @RequestMapping(value = "/organization/user/{id}/profile", method = RequestMethod.GET)
    public String showUserByIdProfile(@PathVariable Long id, UserForm userForm, Model model) {
        model.addAttribute("userForm", userService.findById(id).map(currentLoggedInUser -> {
            userForm.setId(id);
            userForm.setEmail(currentLoggedInUser.getEmail());
            userForm.setFirstName(currentLoggedInUser.getFirstName());
            userForm.setLastName(currentLoggedInUser.getLastName());
            return userForm;
        }).orElseThrow(userNotFound()));

        return "organization/userProfile";
    }
    
    private Supplier<UserNotFoundException> userNotFound() {
        return () -> new UserNotFoundException("Current Logged user may not found or locked or not active");
    }

    private void validate(SignupForm signupForm, BindingResult result) {
        if (isNotEmpty(signupForm.getEmail())) {
            User user = signupService.findByEmail(signupForm.getEmail());

            if (user != null) {
                addFieldError(MODEL_NAME_SIGNUP_DTO, SignupForm.FIELD_NAME_EMAIL, signupForm.getEmail(),
                        ERROR_CODE_EMAIL_EXIST, result);
            }
        }
    }
    
    private void validateRegistation(SignupForm signupForm, BindingResult result) {

        if (isNotEmpty(signupForm.getEmail())) {
            User user = signupService.findByEmail(signupForm.getEmail());

            if (user != null) {
                addFieldError(MODEL_NAME_SIGNUP_DTO, SignupForm.FIELD_NAME_EMAIL, signupForm.getEmail(),
                        ERROR_CODE_EMAIL_EXIST, result);
            }
        }

        if (isEmpty(signupForm.getOrganizationName())) {
            addFieldError(MODEL_NAME_SIGNUP_DTO, SignupForm.FIELD_NAME_ORGANIZATION_NAME,
                    signupForm.getOrganizationName(), ERROR_CODE_ORGANIZATION_NAME_NOT_EMPTY, result);
        }

        if (isNotEmpty(signupForm.getOrganizationName())) {
            signupService.findOrganizationByName(signupForm.getOrganizationName()).ifPresent(organization -> {
                addFieldError(MODEL_NAME_SIGNUP_DTO, SignupForm.FIELD_NAME_ORGANIZATION_NAME,
                        signupForm.getOrganizationName(), ERROR_CODE_ORGANIZATION_NAME_EXIST, result);
            });
        }

        if (isEmpty(signupForm.getPhoneNumber())) {
            addFieldError(MODEL_NAME_SIGNUP_DTO, SignupForm.FIELD_NAME_PHONE_NUMBER,
                    signupForm.getPhoneNumber(), ERROR_CODE_PHONE_NUMBER_NOT_EMPTY, result);
        }
        
    }

    private void validate(OrganizationForm organizationForm, BindingResult result) {
        if (isNotEmpty(organizationForm.getName())) {    	
        	Organization organization = userService.findCurrentLoggedInUser()
                    .map(user -> {
                        Organization org = user.getOrganization();
                        
                        return org;
                    }).orElseThrow(() -> new ResourceNotFoundException("Organization not found by current logged user"));

            if (organizationService.isNameExist(organizationForm.getName(), organization)) {
                addFieldError(MODEL_NAME_ORGANIZATION_FORM_DTO, OrganizationForm.FIELD_NAME_NAME,
                        organizationForm.getName(), ERROR_CODE_ORGANIZATION_NAME_NOT_EMPTY, result);
            }
        }
    }
    
    private void validate(OrganizationForm organizationForm, String slug, BindingResult result) {
        if (isNotEmpty(organizationForm.getName())) {
        	
        	 Organization organization = organizationService.findBySlug(slug)
                     .map(organization1 -> organization1)
                     .orElseThrow(() -> new ResourceNotFoundException("No organization found by- " + slug));

            if (organizationService.isNameExist(organizationForm.getName(), organization)) {
                addFieldError(MODEL_NAME_ORGANIZATION_FORM_DTO, OrganizationForm.FIELD_NAME_NAME,
                        organizationForm.getName(), ERROR_CODE_ORGANIZATION_NAME_NOT_EMPTY, result);
            }
        }
    }
}
