package com.formreleaf.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Bazlur Rahman Rokon
 * @since 10/5/15.
 */
@Controller
@RequestMapping
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        log.info("index");

        return "index";
    }
}
