package com.vantage.sportsregistration.service;

import com.formreleaf.domain.ReportShareSchedule;
import com.formreleaf.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Bazlur Rahman Rokon
 * @since 10/8/15.
 */
@Service
@Transactional
public class ReportShareService {
    @Autowired
    private EmailService emailService;

    @Autowired
    private ReportService reportService;

    public void shareReport(Long reportShareScheduleId) {
        final byte[] contents = reportService.getContentsAsByte(reportShareScheduleId);
        final ReportShareSchedule reportShareSchedule = reportService.findReportShareSchedule(reportShareScheduleId);
        emailService.sendReport(reportShareSchedule, contents);
        reportService.updateReportScheduler(reportShareSchedule);
    }
}
