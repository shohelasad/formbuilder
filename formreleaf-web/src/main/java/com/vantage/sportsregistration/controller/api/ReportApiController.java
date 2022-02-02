package com.vantage.sportsregistration.controller.api;

import com.formreleaf.common.dto.PeopleDTO;
import com.formreleaf.common.dto.ReportDefinitionDto;
import com.formreleaf.domain.Report;
import com.formreleaf.report.service.ReportService;
import com.vantage.sportsregistration.exceptions.UserNotFoundException;
import com.vantage.sportsregistration.service.RegistrationService;
import com.vantage.sportsregistration.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @uathor Md. Asaduzzaman
 * @since 6/16/15.
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping(value = "api/v1/report/")
public class ReportApiController {
    private static Logger LOGGER = LoggerFactory.getLogger(ReportApiController.class);

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public Report saveReport(@Valid @RequestBody ReportDefinitionDto reportDefinitionDto) {
        return userService.findCurrentLoggedInUser()
                .map(user -> reportService.save(reportDefinitionDto, user.getOrganization()))
                .orElseThrow(UserNotFoundException::new);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public Report findReport(@PathVariable Long id) {

        return reportService.findById(id);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<Report> getReports() {

        return userService.findCurrentLoggedInUser()
                .map(user -> reportService.findReportsByOrganization(user.getOrganization()))
                .orElseThrow(UserNotFoundException::new);
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void deleteReport(@PathVariable Long id) {

        reportService.delete(id);
    }

    @RequestMapping(value = "selectable-blocks", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<?> findAllSelectableFields() {

        return userService.findCurrentLoggedInUser()
                .map(user -> reportService.findAllSelectableBlocks(user.getOrganization()))
                .orElseThrow(UserNotFoundException::new);
    }

    @RequestMapping(value = "selectable-programs", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<?> getProgramSections() {

        return userService.findCurrentLoggedInUser()
                .map(reportService::findAllSelectableProgram)
                .orElseThrow(UserNotFoundException::new);
    }

    @RequestMapping(value = "definition", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ReportDefinitionDto getBlackFilter(@RequestParam(value = "reportId") Long reportId) {

        return userService.findCurrentLoggedInUser()
                .map(user -> reportService.getReportDefinition(reportId, user))
                .orElseThrow(UserNotFoundException::new);
    }

    @RequestMapping(value = "names", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<String> findAllNames() {
        Map<PeopleDTO, Long> allPeople = registrationService.findAllPeople();

        return allPeople.keySet()
                .stream()
                .map(PeopleDTO::getPerson).collect(Collectors.toList());
    }

    @RequestMapping(value = "download-csv/{reportId}", method = RequestMethod.GET)
    public ResponseEntity<?> downloadCSV(@PathVariable Long reportId) {

        return reportService.downloadCsv(reportId);
    }

    @RequestMapping(value = "download-pdf/{reportId}", method = RequestMethod.GET)
    public ResponseEntity<?> downloadPdf(@PathVariable Long reportId) {

        return reportService.downloadPdf(reportId);
    }
}
