package com.vantage.sportsregistration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String showHome() {

        return "home";
    }

    @RequestMapping("/admin")
    public String showAdmin() {
        throw new RuntimeException("Test");
       // return "admin";
    }
     
    @RequestMapping("/termsofservice")
    public String showTermsOfServices() {

        return "termsofservice";
    }

}
