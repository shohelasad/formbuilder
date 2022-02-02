package com.vantage.sportsregistration.controller;


import com.formreleaf.domain.Organization;
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

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static com.formreleaf.common.utils.StringUtils.isEmpty;
import static com.formreleaf.common.utils.StringUtils.isNotEmpty;
import static com.formreleaf.common.utils.ValidationUtils.addFieldError;

@Controller
@PreAuthorize("hasAnyRole('ROLE_APPLICATION')")
@RequestMapping("/admin")
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

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

    @RequestMapping(value = "/organization/search", method = RequestMethod.GET)
    public String searchOrganizationByAdmin(Pageable pageable, @RequestParam(value = "q", required = false) String q, Model uiModel) {
        LOGGER.debug("searchOrganization() -> q ={}", q);
        Page<Organization> organizationList = organizationService.findAll(pageable, q.trim().toLowerCase());
        PageWrapper<Organization> page = new PageWrapper<>(organizationList, "/admin/organization/search?q=" + q);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("q", q);
        
        return "admin/organizationList";
    }
    
    @RequestMapping(value = "/organization/list", method = RequestMethod.GET)
    public String findOrganization(Pageable pageable, Model uiModel) {
        Page<Organization> organizationList = organizationService.findAll(pageable);
        PageWrapper<Organization> page = new PageWrapper<>(organizationList, "/admin/organization/list");
        uiModel.addAttribute("page", page);

        return "admin/organizationList";
    }

    @RequestMapping(value = "/organization/{slug}/edit", method = RequestMethod.GET)
    public String editOrganizationBySlug(@PathVariable String slug, Model uiModel) {

        uiModel.addAttribute("organizationForm", organizationService.findBySlug(slug)
                .map(organization -> {
                    OrganizationForm organizationForm = new OrganizationForm();
                    organizationForm.setId(organization.getId());
                    organizationForm.setName(organization.getName());
                    organizationForm.setPhone(organization.getPhone());
                    organizationForm.setFax(organization.getFax());
                    organizationForm.setDescription(organization.getDescription());
                    organizationForm.setWebsite(organization.getWebsite());
                    organizationForm.setSlogan(organization.getSlogan());
                    organizationForm.setSlug(slug);
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
                }).orElseThrow(() -> new ResourceNotFoundException("Organization not found by slug", slug)));

        return "admin/organizationEdit";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLICATION')")
    @RequestMapping(value = "/organization/{slug}/edit", method = RequestMethod.POST)
    public String updateOrganizationBySlug(@PathVariable String slug, @Valid OrganizationForm organizationForm, BindingResult result,
                                     RedirectAttributes redirectAttributes) {

        validate(organizationForm, slug, result);

        if (result.hasErrors()) {

            return "admin/organizationEdit";
        }

        Organization organization = organizationService.saveOrganization(organizationForm);

        redirectAttributes.addFlashAttribute("message", "Organization has been successfully updated");

        return "redirect:/admin/organization/" + organization.getSlug() + "/edit";
    } 
    
    
    @RequestMapping(value = "/organization/delete/{id}", method = RequestMethod.POST)
    public String deleteOrganization(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

    	organizationService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Organization has been deleted");

        return "redirect:/admin/organization/list/";
    }
   
    @RequestMapping(value = "/organization/registration", method = RequestMethod.GET)
    public String registerNewOrganization(SignupForm signupForm) {

        return "admin/organizationRegistration";
    }

    @RequestMapping(value = "/organization/registration", method = RequestMethod.POST)
    public String saveNewOrganization(@Valid SignupForm signupForm, BindingResult result) {
        validateRegistation(signupForm, result);

        if (result.hasErrors()) {

            return "admin/organizationRegistration";
        }

        signupForm.setIsOrganization(true);
        signupService.createNewUser(signupForm);

        return "redirect:/admin/organization/list";
    }
    
   
    @RequestMapping(value = "/organization/{slug}/new-user", method = RequestMethod.GET)
    public String addNewUserToOrganizationBySlug(@PathVariable String slug, SignupForm signupForm, Model uiModel) {
    	signupForm.setSlug(slug);
    	uiModel.addAttribute("signupForm", signupForm);
        
    	return "admin/organizationNewUser";
    }

    @RequestMapping(value = "/organization/{slug}/new-user", method = RequestMethod.POST)
    public String saveNewUserBySlug(@Valid SignupForm signupForm, BindingResult result) {
        validate(signupForm, result);

        if (result.hasErrors()) {

            return "admin/organizationNewUser";
        }

        organizationService.addNewUserToOrganizationBySlug(signupForm);

        return "redirect:/admin/organization/" + signupForm.getSlug() + "/user-list";
    }

    
    @RequestMapping(value = "/organization/{slug}/user-list", method = RequestMethod.GET)
    public String findOrganizationUsers(@PathVariable String slug, Model uiModel) {
    	OrganizationForm organizationForm = new OrganizationForm();
    	organizationForm.setSlug(slug);
    	uiModel.addAttribute("organizationForm", organizationForm);
        uiModel.addAttribute("users", organizationService.findAllUsers(slug));

        return "admin/organizationUserList";
    }
    
    @RequestMapping(value = "/organization/{slug}/user/lock/{id}", method = RequestMethod.POST)
    public String lockUserByAdmin(@PathVariable String slug, @PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

    	userService.lock(id);
        redirectAttributes.addFlashAttribute("message", "User has been locked");

        return "redirect:/admin/organization/" + slug + "/user-list";
    }
    
    @RequestMapping(value = "/organization/{slug}/user/unlock/{id}", method = RequestMethod.POST)
    public String unlockUserByAdmin(@PathVariable String slug, @PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

    	userService.unlock(id);
        redirectAttributes.addFlashAttribute("message", "User has been unlocked");

        return "redirect:/admin/organization/" + slug + "/user-list";
    }
      
    @RequestMapping(value = "/user/list", method = RequestMethod.GET)
    public String showUserList(Model model, Pageable pageable) {

        Page<User> userList = userService.findAllParentUser(pageable);
        model.addAttribute("page", new PageWrapper<>(userList, "/admin/user/list"));

        return "admin/parentUserList";
    }
    
    @RequestMapping(value = "/user/search", method = RequestMethod.GET)
    public String searchParentUserByAdmin(Pageable pageable, @RequestParam(value = "q", required = false) String q, Model uiModel) {
        LOGGER.debug("searchOrganization() -> q ={}", q);
        Page<User> userList = userService.findAllParentUser(pageable, q);
        PageWrapper<User> page = new PageWrapper<>(userList, "/admin/user/search?q=" + q);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("q", q);
        
        return "admin/parentUserList";
    }

    @RequestMapping(value = "/user/lock/{id}", method = RequestMethod.POST)
    public String lockUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

    	userService.lock(id);
        redirectAttributes.addFlashAttribute("message", "User has been locked");

        return "redirect:/admin/user/list/";
    }
    
    @RequestMapping(value = "/user/unlock/{id}", method = RequestMethod.POST)
    public String unlockUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

    	userService.unlock(id);
        redirectAttributes.addFlashAttribute("message", "User has been unlocked");

        return "redirect:/admin/user/list/";
    }
    
    @RequestMapping(value = "/user/{id}/profile", method = RequestMethod.GET)
    public String showUserByIdProfile(@PathVariable Long id, UserForm userForm, Model model) {
        model.addAttribute("userForm", userService.findById(id).map(currentLoggedInUser -> {
        	userForm.setId(id);
            userForm.setEmail(currentLoggedInUser.getEmail());
            userForm.setFirstName(currentLoggedInUser.getFirstName());
            userForm.setLastName(currentLoggedInUser.getLastName());
            return userForm;
        }).orElseThrow(userNotFound()));

        return "admin/userProfile";
    }
    
    private Supplier<UserNotFoundException> userNotFound() {
        return () -> new UserNotFoundException("Current Logged user may not found or locked or not active");
    }
    
    @RequestMapping(value = "/user/{id}/profile", method = RequestMethod.POST)
    public String saveUserProfile(@PathVariable Long id, Model model, @Valid UserForm userForm, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
        	return "admin/userProfile";
        }

        userService.updateUser(userForm);

        redirectAttributes.addFlashAttribute("message", "User has been updated successfully");

        return "redirect:/admin/user/" + userForm.getId() +"/profile";
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

    
    private void validate(OrganizationForm organizationForm, String slug, BindingResult result) {
        if (isNotEmpty(organizationForm.getName())) {
        	
        	 Organization organization = organizationService.findBySlug(slug)
                     .map(organization1 -> organization1)
                     .orElseThrow(() -> new ResourceNotFoundException("No organization found by- " + slug));
        	
        	//When admin want to edit any organization, check organization name can not be empty
            if (organizationService.isNameExist(organizationForm.getName(), organization)) {
                addFieldError(MODEL_NAME_ORGANIZATION_FORM_DTO, OrganizationForm.FIELD_NAME_NAME,
                        organizationForm.getName(), ERROR_CODE_ORGANIZATION_NAME_NOT_EMPTY, result);
            }
        }
    }

}
