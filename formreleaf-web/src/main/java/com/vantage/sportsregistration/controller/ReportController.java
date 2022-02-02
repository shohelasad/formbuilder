package com.vantage.sportsregistration.controller;

import com.formreleaf.common.utils.StringUtils;
import com.formreleaf.domain.Report;
import com.formreleaf.domain.ReportShareSchedule;
import com.formreleaf.report.service.ReportService;
import com.vantage.sportsregistration.service.ReportShareService;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportShareService reportShareService;

    @RequestMapping(value = "/report/list", method = RequestMethod.GET)
    public String showReports(Model model) {

        return "report/list";
    }
    
    @RequestMapping(value = "/report/confirmation", method = RequestMethod.GET)
    public String showReportsConfirmation(Model model, RedirectAttributes redirectAttributes) {
    	redirectAttributes.addFlashAttribute("message", "Report has been created");

		return "redirect:/report/list";
    }

    @RequestMapping(value = "/report/create", method = RequestMethod.GET)
    public String createReport() {

        return "report/reportDefinition";
    }

    @RequestMapping(value = "/report/edit/{id}", method = RequestMethod.GET)
    public String editReport(@PathVariable Long id, Model uiModel) {
        uiModel.addAttribute("reportId", id);

        return "report/reportDefinition";
    }

    @RequestMapping(value = "/report/clone/{id}", method = RequestMethod.GET)
    public String cloneReport(@PathVariable Long id, Model uiModel) {
        uiModel.addAttribute("id", id);

        return "report/reportDefinition";
    }

    @RequestMapping(value = "/report/{reportId}/share", method = RequestMethod.GET)
    public String shareReport(@PathVariable Long reportId, ReportShareSchedule reportShareSchedule) {
        Report report = reportService.findOne(reportId);
        reportShareSchedule.setReport(report);

        return "report/share";
    }

    @RequestMapping(value = "/report/share", method = RequestMethod.POST)
    public String saveReportSchedule(@Valid ReportShareSchedule reportShareSchedule, BindingResult bindingResult) {

        validateEmails(reportShareSchedule, bindingResult);

        if (bindingResult.hasErrors()) {
            Report report = reportService.findOne(reportShareSchedule.getReport().getId());
            reportShareSchedule.setReport(report);

            return "report/share";
        }

        ReportShareSchedule saved = reportService.saveReportSchedule(reportShareSchedule);

        return "redirect:/report/" + saved.getReport().getId() + "/share/list";
    }


    @RequestMapping(value = "/report/share/{reportShareScheduleId}/edit", method = RequestMethod.GET)
    public String editReportScheduleForm(@PathVariable Long reportShareScheduleId, Model uiModel) {
        ReportShareSchedule reportShareSchedule = reportService.findReportShareSchedule(reportShareScheduleId);
        reportShareSchedule.setEmails(String.join(", ", reportShareSchedule.getRecipientEmails()));

        uiModel.addAttribute("reportShareSchedule", reportShareSchedule);

        return "report/share";
    }


    private void validateEmails(ReportShareSchedule reportShareSchedule, final BindingResult bindingResult) {
        String emails = reportShareSchedule.getEmails();
        if (StringUtils.isNotEmpty(emails)) {
            String[] split = emails.split(",");

            Arrays.asList(split).forEach(email -> {
                if (StringUtils.isNotEmpty(email.trim()) && !EmailValidator.getInstance().isValid(email.trim())) {
                    log.info("{} is not valid email address", email);

                    FieldError fieldError = new FieldError("reportShareSchedule", "emails", String.format("'%s' is not a valid email address", email.trim()));
                    bindingResult.addError(fieldError);
                }
            });
        }
    }

    @RequestMapping(value = "/report/{reportId}/share/list", method = RequestMethod.GET)
    public String shareReportList(@PathVariable Long reportId, Model uiModel) {
        List<ReportShareSchedule> schedules = reportService.findAllReportShareSchedule(reportId);
        uiModel.addAttribute("schedules", schedules);
        uiModel.addAttribute("reportId", reportId);
        uiModel.addAttribute("reportName", reportService.findOne(reportId).getName());

        return "report/shareList";
    }

    @RequestMapping(value = "/report/share/{reportShareScheduleId}/delete", method = RequestMethod.POST)
    public ResponseEntity deleteShareSchedule(@PathVariable Long reportShareScheduleId) {
        reportService.deleteReportShareSchedule(reportShareScheduleId);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/report/share/{reportShareScheduleId}/now", method = RequestMethod.POST)
    public ResponseEntity shareNow(@PathVariable Long reportShareScheduleId) {

        reportShareService.shareReport(reportShareScheduleId);

        return new ResponseEntity(HttpStatus.OK);
    }
}
