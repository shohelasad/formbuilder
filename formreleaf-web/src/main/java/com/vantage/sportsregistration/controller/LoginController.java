package com.vantage.sportsregistration.controller;

import com.vantage.sportsregistration.exceptions.UserNotFoundException;
import com.vantage.sportsregistration.service.UserService;

import com.formreleaf.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/5/15.
 */
@Controller
public class LoginController {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
	
	protected static final String SAVED_REQUEST = "url_prior_auth";

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String signin(Model model, HttpServletRequest request) {

		if (userService.isAuthenticated()) {
			return "redirect:/";
		}
		
		//Save REFERER url to session
	    String referrer = request.getHeader(HttpHeaders.REFERER);
	    if(StringUtils.isNotEmpty(referrer) && referrer.contains("/organizations")) {
	    	request.getSession().setAttribute(SAVED_REQUEST, referrer);
	    }
		
		return "login";
	}

	@RequestMapping(value = "/login-error/{errorType}", method = RequestMethod.GET)
	public String signinAuthenticationFailure(@PathVariable String errorType, Model model, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		LOGGER.info("signinAuthenticationFailure, errorType={}", errorType);

		redirectAttributes.addFlashAttribute("errorType", errorType);
		redirectAttributes.addFlashAttribute("userEmail", request.getParameter("userEmail"));

		return "redirect:/login";
	}

	@RequestMapping("/switchuserExit")
	public String switchUserExit() {
		LOGGER.info("[event:IMPERSONATE_USER] Impersonating is exit for user={}",
				userService.findCurrentLoggedInUser().map(u -> u.getEmail()).orElseThrow(userNotFound()));

		return "redirect:/switchuserlogout";
	}

	private Supplier<UserNotFoundException> userNotFound() {
		return () -> new UserNotFoundException("Current Logged user may not found or locked or not active");
	}

}
