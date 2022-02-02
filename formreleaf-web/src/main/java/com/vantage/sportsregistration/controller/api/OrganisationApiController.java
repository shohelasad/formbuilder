package com.vantage.sportsregistration.controller.api;

import com.formreleaf.domain.Agreement;
import com.formreleaf.domain.Policy;
import com.vantage.sportsregistration.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 4/26/15.
 */
@RestController
@RequestMapping(value = "api/v1/organization/")
public class OrganisationApiController {

    @Autowired
    private OrganizationService organizationService;

    //<editor-fold desc="Agreements">
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "agreement", method = RequestMethod.POST)
    public Agreement saveAgreement(@Valid @RequestBody Agreement agreement) {

        return organizationService.updateOrganizationAgreements(agreement);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "agreement", method = RequestMethod.PUT)
    public Agreement editAgreement(@Valid @RequestBody Agreement agreement) {

        return organizationService.updateOrganizationAgreements(agreement);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "agreements", method = RequestMethod.GET)
    public Set<Agreement> findAllAgreements() {

        return organizationService.findAllAgreements();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @RequestMapping(value = "agreement/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAgreement(@PathVariable Long id) {

        organizationService.deleteAgreement(id);
    }
    //</editor-fold>

    //<editor-fold desc="Policy">
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "policy", method = RequestMethod.POST)
    public Policy savePolicy(@Valid @RequestBody Policy policy) {

        return organizationService.saveOrganizationPolicy(policy);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "policy", method = RequestMethod.PUT)
    public Policy editPolicy(@Valid @RequestBody Policy policy) {

        return organizationService.saveOrganizationPolicy(policy);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "policies", method = RequestMethod.GET)
    public Set<Policy> findAllPolicies() {

        return organizationService.findAllPolicies();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @RequestMapping(value = "policy/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePolicy(@PathVariable Long id) {

        organizationService.deletePolicy(id);
    }
    //</editor-fold>


}
