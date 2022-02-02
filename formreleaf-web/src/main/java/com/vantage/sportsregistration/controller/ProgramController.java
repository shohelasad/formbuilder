package com.vantage.sportsregistration.controller;


import com.formreleaf.common.dto.PeopleDTO;
import com.formreleaf.common.utils.PageWrapper;
import com.formreleaf.domain.Program;
import com.formreleaf.domain.Registration;
import com.vantage.sportsregistration.service.ProgramService;
import com.vantage.sportsregistration.service.RegistrationService;
import com.vantage.sportsregistration.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * @author Bazlur Rahman Rokon
 * @since 4/13/15.
 */

@Controller
public class ProgramController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramController.class);

    private static final String VIEW_NAME_PROGRAM_LIST_PAGE = "program/list";
    private static final String VIEW_NAME_PROGRAM_SHOW_PAGE = "program/show";
    public static final String CURRENT_EDITING_PROGRAM_ID = "current_editing_program_id";

    @Autowired
    private ProgramService programService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;
 

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "program/create/overview")
    public String createNewProgram(Program program) {

        return "program/overview";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/program/create/overview", method = RequestMethod.POST)
    public String saveProgramOverview(@Valid Program program, BindingResult result) {
        if (result.hasErrors()) {

            return "program/overview";
        }

        Program programSaved = programService.saveProgramOverview(program);

        return String.format("redirect:/program/create/%d/%s", programSaved.getId(), "location");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "program/create/{programId}/{page}", method = RequestMethod.GET)
    public String createNewProgram(@PathVariable(value = "programId") Long programId,
                                   @PathVariable(value = "page") String page,
                                   Model uiModel) {

        Program program = programService.findById(programId);
        checkOwnerShip(program);

        uiModel.addAttribute("program", program);

        if (page.equals("agreement")) {
            uiModel.addAttribute("organization", program.getOrganization());
        }

        return "program/" + page;
    }

    //TODO revisit: this method is redundant, can be merged with createNewProgram()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/program/create/{programId}/{page}", method = RequestMethod.POST)
    public String saveProgram(@PathVariable Long programId,
                              @PathVariable String page,
                              @Valid Program program,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        switch (page) {

            case "publish":
                programService.updateProgramPublish(program);
                redirectAttributes.addFlashAttribute("message", "Program has been published");

                return "redirect:/program/list";
        }

        return "redirect:/program/create/overview";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/program/list", method = RequestMethod.GET)
    public String showProgramList(Model model, Pageable pageable) {
        Page<Program> programList = programService.findAll(pageable);

        PageWrapper<Program> page = new PageWrapper<>(programList, "/program/list");
        model.addAttribute("page", page);

        return VIEW_NAME_PROGRAM_LIST_PAGE;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/program/registrants", method = RequestMethod.GET)
    public String showRegistrantsList(Pageable pageable, Model uiModel) {
        Page<PeopleDTO> peopleList = registrationService.findAllPeople(pageable);
        PageWrapper<PeopleDTO> page = new PageWrapper<>(peopleList, "/program/registrants");
        uiModel.addAttribute("page", page);

        return "program/people";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/program/registrant/search", method = RequestMethod.GET)
    public String searchRegistrants(Pageable pageable, @RequestParam(value = "q", required = false) String q, Model uiModel) {
        LOGGER.debug("searchPeople() -> q ={}", q);
        Page<PeopleDTO> peopleList = registrationService.findAllPeople(pageable, q);
        PageWrapper<PeopleDTO> page = new PageWrapper<>(peopleList, "/program/registrant/search?q=" + q);
        uiModel.addAttribute("page", page);
        uiModel.addAttribute("q", q);

        return "program/people";
    }


    @RequestMapping(value = "/program/show/{id}", method = RequestMethod.GET)
    public String show(@PathVariable Long id) {

        return VIEW_NAME_PROGRAM_SHOW_PAGE;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "program/edit/{id}", method = RequestMethod.GET)
    public String editProgram(@PathVariable("id") Long id, Model model) {

        Program program = programService.findById(id);
        checkOwnerShip(program);

        model.addAttribute("program", program);

        return String.format("redirect:/program/create/%d/%s", program.getId(), "overview");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/program/clone/{id}", method = RequestMethod.GET)
    public String cloneProgram(Program program, @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

        program = programService.findById(id);
        checkOwnerShip(program);
        program = programService.cloneNewProgram(program);
        model.addAttribute("program", program);
        redirectAttributes.addFlashAttribute("message", "You have cloned program: " + program.getName());

        return String.format("redirect:/program/create/%d/%s", program.getId(), "overview");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/program/delete/{id}", method = RequestMethod.POST)
    public String deleteProgram(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

        Program program = programService.findById(id);
        checkOwnerShip(program);

        programService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Program has been deleted");

        return "redirect:/program/list/";
    }

    @RequestMapping(value = "organizations/{oslug}/programs/{pslug}", method = RequestMethod.GET)
    public String showProgramView(@PathVariable String oslug,
                                  @PathVariable String pslug,
                                  Model uiModel,
                                  HttpServletRequest request) {

        Program program = programService.findBySlug(oslug, pslug);
        
        //TODO: Fix code for showing registration history	
        boolean isAuthenticated = userService.isAuthenticated();
        if (isAuthenticated && userService.isParentUser()) {
            final Object currentEditingProgramId = request.getSession().getAttribute(CURRENT_EDITING_PROGRAM_ID);
            if (program.getId().equals(currentEditingProgramId)) {
                final Optional<Long> currentEditingRegistrationId = registrationService.findCurrentEditingRegistrationId(program.getId());
                if (currentEditingRegistrationId.isPresent()) {

                    return "redirect:/edit-registration/" + program.getSlug() + "?registrationId=" + currentEditingRegistrationId.get();
                }
            } 
            else {
                request.getSession().setAttribute(CURRENT_EDITING_PROGRAM_ID, program.getId());
            }
        }
        
        uiModel.addAttribute("program", program);
        uiModel.addAttribute("organization", program.getOrganization());
        uiModel.addAttribute("isAuthenticated", isAuthenticated);

        return "program/publicView";
    }

    @RequestMapping(value = "program/registrations", method = RequestMethod.GET)
    public String showRegistrationHistory(@RequestParam(value = "name", required = true) String name, Model uiModel) {
        Set<Registration> registrations = programService.findAllByRegistrantName(name);
        uiModel.addAttribute("registrations", registrations);

        return "registration/registrationHistory";
    }

    //TODO move To Registration Controller
    @RequestMapping(value = "edit-registration/{slug}", method = RequestMethod.GET)
    public String editProgramPublicView(@PathVariable String slug, @RequestParam(value = "registrationId", required = true) Long registrationId, Model uiModel) {
        Registration registration = programService.findRegistrationById(registrationId);
        checkOwnerShip(registration);

        uiModel.addAttribute("program", registration.getProgram());
        uiModel.addAttribute("organization", registration.getProgram().getOrganization());
        uiModel.addAttribute("registration", registration);

        return "program/publicView";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "program/{id}/view-registrants", method = RequestMethod.GET)
    public String viewRegistrants(@PathVariable Long id, Model uiModel, Pageable pageable) {
        Program program = programService.findById(id);
        checkOwnerShip(program);

        uiModel.addAttribute("program", program);

        return "program/registrants";
    }

    private void checkOwnerShip(Registration registration) {
        List<Long> registrationIds = registrationService.findAllRegistrationIdsOfCurrentLoggedInUser();

        if (!registrationIds.stream().anyMatch(aLong -> aLong.equals(registration.getId()))) {
            throw new AccessDeniedException("You don't have sufficient permission to access this resource");
        }
    }

    private void checkOwnerShip(Program program) {
        List<Long> programIds = programService.findAllProgramIdsOfCurrentLoggedInOrganization();

        if (!programIds.stream().anyMatch(aLong -> aLong.equals(program.getId()))) {
            throw new AccessDeniedException("You don't have sufficient permission to access this resource");
        }
    }
}