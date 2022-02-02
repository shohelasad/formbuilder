package com.vantage.sportsregistration.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class RecaptchaService {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(RecaptchaService.class);
	 
	 @Resource
	 private Environment environment;
	 
	 public boolean isCaptchaResponseValid(String recaptchaResponse, HttpServletRequest request) {
		 boolean successStatus =  false;
		 
		 if (recaptchaResponse.isEmpty()) {
		     
		      return false;
		 }
		 
		 try {
	        //Send get request to Google reCaptcha server with secret key
	        URL url = new URL( environment.getProperty("recaptcha.site.url") + "?secret=" +  
	        		environment.getProperty("recaptcha.private.key") + "&response=" + recaptchaResponse + 
	        		"&remoteip=" + request.getRemoteAddr());  
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");
	        String line, outputString = "";

	        BufferedReader reader = new BufferedReader(
	                new InputStreamReader(connection.getInputStream()));
	        while ((line = reader.readLine()) != null) {
	        	outputString += line;
	        }

	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode rootNode = objectMapper.readTree(outputString.toString());     
	      	JsonNode rspNode = rootNode.path("success");
	      	successStatus = rspNode.asBoolean();
	    }
	    catch(Exception e){
	    	LOGGER.error("Unable validate recaptcha response: ", e);
	    	
	        return false;
	    }
 
	    return successStatus;
	 }	 
}

