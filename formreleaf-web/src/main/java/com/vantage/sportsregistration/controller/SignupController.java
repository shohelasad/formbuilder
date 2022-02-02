package com.vantage.sportsregistration.controller;

import com.formreleaf.domain.User;
import com.vantage.sportsregistration.dto.SignupForm;
import com.vantage.sportsregistration.service.SignupService;

import com.formreleaf.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.formreleaf.common.utils.StringUtils.isEmpty;
import static com.formreleaf.common.utils.StringUtils.isNotEmpty;
import static com.formreleaf.common.utils.ValidationUtils.addFieldError;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/4/15.
 */

@Controller
public class SignupController {

    protected static final String MODEL_NAME_SIGNUP_DTO = "signupForm";
    protected static final String ERROR_CODE_EMAIL_EXIST = "NotExist.user.email";
    protected static final String ERROR_CODE_ORGANIZATION_NAME_EXIST = "NotExist.organizationForm.name";
    protected static final String ERROR_CODE_ORGANIZATION_NAME_NOT_EMPTY = "NotEmpty.signupForm.organizationName";
    protected static final String ERROR_CODE_PHONE_NUMBER_NOT_EMPTY = "NotEmpty.signupForm.phoneNumber";
    protected static final String SAVED_REQUEST = "url_prior_auth";


    @Autowired
    private SignupService signupService;
    

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String showSignUpForm(SignupForm signupForm, HttpServletRequest request) {
    	
    	//Save REFERER url to session
	    String referrer = request.getHeader(HttpHeaders.REFERER);
	    if(StringUtils.isNotEmpty(referrer) && referrer.contains("/organizations")) {
	    	request.getSession().setAttribute(SAVED_REQUEST, referrer);
	    }
    	
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String submitSignUpForm(@Valid SignupForm signupForm, BindingResult bindingResult) {

        validate(signupForm, bindingResult);

        if (bindingResult.hasErrors()) {

            return "signup";
        }

        signupService.createNewUser(signupForm);

        return "redirect:/login";
    }
	
    private void validate(SignupForm signupForm, BindingResult result) {

        if (isNotEmpty(signupForm.getEmail())) {
            User user = signupService.findByEmail(signupForm.getEmail());

            if (user != null) {
                addFieldError(MODEL_NAME_SIGNUP_DTO, SignupForm.FIELD_NAME_EMAIL, signupForm.getEmail(),
                        ERROR_CODE_EMAIL_EXIST, result);
            }
        }

        if (signupForm.getIsOrganization()) {

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
    }
}
