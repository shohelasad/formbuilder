package com.vantage.sportsregistration.controller.api;

import com.formreleaf.common.dto.ApplicationAbout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.lang.management.ManagementFactory;
import java.time.Duration;

import static java.lang.Runtime.getRuntime;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 5/10/15.
 */

@RestController
@RequestMapping("/api/v1/about")
public class MiscApiController {

    @Autowired
    private ServletContext servletContext;

    @RequestMapping(method = RequestMethod.GET)
    public ApplicationAbout getAbout() {
        final ApplicationAbout about = new ApplicationAbout();
        final ApplicationAbout.VMProperties vMProperties = new ApplicationAbout.VMProperties();

        final Runtime runtime = getRuntime();
        final long freeMemory = runtime.freeMemory();
        vMProperties.setTotalMemory(runtime.totalMemory());
        vMProperties.setUsedMemory(runtime.totalMemory() - freeMemory);
        vMProperties.setFreeMemory(freeMemory);
        vMProperties.setMaxMemory(runtime.maxMemory());
        vMProperties.setUptime(Duration.ofSeconds(ManagementFactory.getRuntimeMXBean().getUptime() / 1000));

        about.setVm(vMProperties);
        about.setServerName(servletContext.getServerInfo());

        return about;
    }
}
