package com.vantage.sportsregistration.controller;

import com.formreleaf.domain.PasswordResetToken;
import com.formreleaf.domain.User;
import com.vantage.sportsregistration.dto.PasswordChangeForm;
import com.vantage.sportsregistration.dto.PasswordResetForm;
import com.vantage.sportsregistration.service.EmailService;
import com.vantage.sportsregistration.service.RecaptchaService;
import com.vantage.sportsregistration.service.SignupService;
import com.vantage.sportsregistration.service.UserService;
import com.formreleaf.common.utils.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.UUID;


@Controller
public class PasswordResetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SignupService signupService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RecaptchaService recaptchaService;

    @Resource
    private Environment environment;

    @RequestMapping(value = "/reset-password")
    public String getPasswordResetRequestForm(PasswordResetForm passwordResetForm) {

        return "resetPassword";
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public String requestPasswordReset(@Valid PasswordResetForm passwordResetForm, BindingResult result, HttpServletRequest request,
                                       final @RequestParam("g-recaptcha-response") String gRecaptchaResponse, RedirectAttributes redirectAttributes) {

        try {
            if (!recaptchaService.isCaptchaResponseValid(gRecaptchaResponse, request)) {
                redirectAttributes.addFlashAttribute("error", "Unable to pass recaptcha challenge. Please try again.");

                return "redirect:/reset-password";
            }
        } catch (Exception e) {
            LOGGER.error("Unable to pass recaptcha challenge: ", e);
            redirectAttributes.addFlashAttribute("error", "Unable to pass recaptcha challenge. Please try again.");

            return "redirect:/reset-password";
        }

        if (result.hasErrors()) {

            return "resetPassword";
        }

        try {
            User user = signupService.findByEmail(passwordResetForm.getEmail());
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "No user with the provided email address exists.");

                return "redirect:/reset-password";
            }

            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            emailService.sendResetPasswordNotification(user, ServletUtils.getContextURL(""), token);
            redirectAttributes.addFlashAttribute("message", "We've sent instructions for resetting your password to the email address you provided.");
        } catch (Exception e) {
            LOGGER.error("Unable to process password reset request: ", e);
            redirectAttributes.addFlashAttribute("message", "Your password reset request could not be processed. Please try again later.");
        }

        return "redirect:/reset-password";
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.GET)
    public String showChangePasswordPage(RedirectAttributes redirectAttributes, PasswordChangeForm passwordChangeForm,
    		@RequestParam("token") String token) {

        PasswordResetToken passworResetToken = userService.getPasswordResetToken(token);
        if (passworResetToken == null) {
        	redirectAttributes.addFlashAttribute("error", "The password reset link was invalid, possibly because the link has already been used.");

            return "redirect:/reset-password";
        }

        Calendar cal = Calendar.getInstance();
        if ((passworResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) < 0) {
        	redirectAttributes.addFlashAttribute("error", "The password reset link was invalid, possibly because the link has already been expired.");

            return "redirect:/reset-password";
        }

        passwordChangeForm.setPasswordResetToken(token);

        return "changePassword";
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    public String savePassword(@Valid PasswordChangeForm passwordChangeForm, BindingResult result, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {

            return "changePassword";
        }

        User  user = userService.getUserByPasswordResetToken(passwordChangeForm.getPasswordResetToken());
        //After resetting password user gets authority for auto login
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, userService.loadUserByUsername(user.getEmail()).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        userService.changeUserPassword(user, passwordChangeForm.getNewPassword());

        return "redirect:/login";
    }
}
