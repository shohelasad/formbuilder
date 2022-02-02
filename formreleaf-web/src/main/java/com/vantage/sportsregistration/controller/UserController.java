package com.vantage.sportsregistration.controller;

import com.formreleaf.domain.User;
import com.vantage.sportsregistration.dto.PasswordForm;
import com.vantage.sportsregistration.dto.UserForm;
import com.vantage.sportsregistration.exceptions.UserNotFoundException;
import com.vantage.sportsregistration.service.UserService;
import com.formreleaf.common.utils.PageWrapper;
import com.formreleaf.common.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.function.Supplier;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final String VIEW_NAME_TERMS_SERVICE_PAGE = "termsOfService";
    private static final String VIEW_NAME_USER_PROFILE_PAGE = "user/userProfile";

    @Autowired
    private UserService userService;

    @Autowired
    private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

    @RequestMapping(value = "/termsofservice", method = RequestMethod.GET)
    public String showTermsOfService() {

        return VIEW_NAME_TERMS_SERVICE_PAGE;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String showUserProfile(UserForm userForm, Model model) {

        model.addAttribute("userForm", userService.findCurrentLoggedInUser().map(currentLoggedInUser -> {
            userForm.setId(currentLoggedInUser.getId());
            userForm.setEmail(currentLoggedInUser.getEmail());
            userForm.setFirstName(currentLoggedInUser.getFirstName());
            userForm.setLastName(currentLoggedInUser.getLastName());
            return userForm;
        }).orElseThrow(userNotFound()));

        return VIEW_NAME_USER_PROFILE_PAGE;
    }

    @RequestMapping(value = "/{id}/profile", method = RequestMethod.GET)
    public String showUserByIdProfile(@PathVariable Long id, UserForm userForm, Model model) {

        model.addAttribute("userForm", userService.findById(id).map(user -> {
            checkOwnerShip(user);

            userForm.setId(id);
            userForm.setEmail(user.getEmail());
            userForm.setFirstName(user.getFirstName());
            userForm.setLastName(user.getLastName());
            return userForm;
        }).orElseThrow(userNotFound()));

        return VIEW_NAME_USER_PROFILE_PAGE;
    }

    private Supplier<UserNotFoundException> userNotFound() {
        return () -> new UserNotFoundException("Current Logged user may not found or locked or not active");
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String saveUserProfile(Model model, @Valid UserForm userForm, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {

            return VIEW_NAME_USER_PROFILE_PAGE;
        }

        userService.updateUser(userForm);

        redirectAttributes.addFlashAttribute("message", "User has been updated successfully");

        return "redirect:/user/profile";
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.GET)
    public String getPasswordForm(PasswordForm passwordForm) {

        return "user/changePassword";
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    public String changePassword(@Valid PasswordForm passwordForm, BindingResult result, HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        validate(passwordForm, result);

        if (result.hasErrors()) {

            return "user/changePassword";
        }

        userService.changePassword(passwordForm.getNewPassword());
        try {
            request.logout();
        } catch (ServletException e) {
            LOGGER.error("unable to logout", e);
        }

        return "redirect:/login";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/{page}", method = RequestMethod.GET)
    public String showProfile(Model model, @PathVariable(value = "page") String page) {

        model.addAttribute("profile", page);

        return "user/participant";
    }

    @RequestMapping(value = "/{id}/{page}", method = RequestMethod.GET)
    public String showProfileById(Model model, @PathVariable(value = "page") String page) {

        model.addAttribute("profile", page);

        return "user/participant";
    }

    private void validate(PasswordForm passwordForm, BindingResult result) {
        userService.findCurrentLoggedInUser().ifPresent(user -> {
            if (passwordForm.getCurrentPassword() != null) {
                if (!messageDigestPasswordEncoder.encodePassword(passwordForm.getCurrentPassword(), user.getSalt()).equals(user.getHashedPassword())) {
                    ValidationUtils.addFieldError("passwordForm", "currentPassword", passwordForm.getCurrentPassword(), "current.password.not.valid", result);
                }
            }
        });
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLICATION')")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String showUserList(Model model, Pageable pageable) {

        Page<User> userList = userService.findAll(pageable);
        model.addAttribute("page", new PageWrapper<>(userList, "/user/list"));

        return "user/list";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLICATION')")
    @RequestMapping(value = "/lock/{id}", method = RequestMethod.POST)
    public String lockUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

        userService.lock(id);
        redirectAttributes.addFlashAttribute("message", "User has been locked");

        return "redirect:/user/list/";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPLICATION')")
    @RequestMapping(value = "/unlock/{id}", method = RequestMethod.POST)
    public String unlockUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

        userService.unlock(id);
        redirectAttributes.addFlashAttribute("message", "User has been unlocked");

        return "redirect:/user/list/";
    }

    private void checkOwnerShip(User user) {

        userService.findCurrentLoggedInUser().map(currentLoggedInUser -> {
            boolean isAdminUser = userService.isCurrentLoggedInUserIsOrganization();

            if (!isAdminUser && !user.equals(currentLoggedInUser)) {
                throw new AccessDeniedException("You don't have access to this resource");
            } else return true;
        }).orElseThrow(userNotFound());
    }
}
