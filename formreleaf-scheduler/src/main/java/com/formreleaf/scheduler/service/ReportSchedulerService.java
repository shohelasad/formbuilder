package com.formreleaf.scheduler.service;

import com.formreleaf.domain.ReportShareSchedule;
import com.formreleaf.domain.enums.ReportFormat;
import com.formreleaf.domain.enums.ReportSharingFrequency;
import com.formreleaf.report.service.ReportService;
import com.formreleaf.repository.ReportShareScheduleRepository;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Bazlur Rahman Rokon
 * @since 10/7/15.
 */
@Service
public class ReportSchedulerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportSchedulerService.class);

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportShareScheduleRepository reportShareScheduleRepository;

    @Autowired
    private EmailService emailService;

    public void onReportExpiredInterval() {
        LOGGER.info("[event:OFFLINE_REPORT] onReportExpiredInterval()");

        reportService.updateExpiredScheduler();
    }

    @Transactional
    public void onRefreshReportShareInterval() {
        LOGGER.info("[event:OFFLINE_REPORT] onRefreshReportShareInterval()");

        LocalDateTime now = LocalDateTime.now();

        final List<ReportShareSchedule> totalSchedules = reportShareScheduleRepository.findAllByDeletedFalseAndExpiredFalse();

        LOGGER.info("Total Schedules : {}", totalSchedules.size());
        final List<ReportShareSchedule> filterSchedules = totalSchedules
                .stream()
                .filter(reportShareSchedule -> filterReportShareScheduler(now, reportShareSchedule))
                .collect(Collectors.toList());

        LOGGER.info("Total Schedules (filtered) : {}", filterSchedules.size());
        filterSchedules.forEach(this::compute);
    }

    private boolean filterReportShareScheduler(LocalDateTime now, ReportShareSchedule reportShareSchedule) {
        ReportSharingFrequency reportSharingFrequency = reportShareSchedule.getReportSharingFrequency();
        if (reportSharingFrequency == ReportSharingFrequency.DAILY) {
            return true;
        } else if (reportSharingFrequency == ReportSharingFrequency.WEEKLY) {
            LocalDateTime lastRunDate = reportShareSchedule.getLastRunDate();
            if (lastRunDate == null) return true;
            int days = Days.daysBetween(lastRunDate, now).getDays();

            return days >= 7;

        } else if (reportSharingFrequency == ReportSharingFrequency.MONTHLY) {
            LocalDateTime lastRunDate = reportShareSchedule.getLastRunDate();
            if (lastRunDate == null) return true;

            int days = Days.daysBetween(lastRunDate, now).getDays();

            return days >= 30;
        }

        return false;
    }

    @Transactional()
    private void compute(ReportShareSchedule reportShareSchedule) {
        try {
            ReportFormat reportFormat = reportShareSchedule.getReportFormat();

            switch (reportFormat) {
                case CSV:
                    byte[] csvContent = reportService.getCsvContent(reportShareSchedule.getReport());
                    emailService.sendReport(reportShareSchedule, csvContent);
                    reportService.updateReportScheduler(reportShareSchedule);
                    break;
                case PDF:
                    byte[] pdfContent = reportService.getPdfContent(reportShareSchedule.getReport());
                    emailService.sendReport(reportShareSchedule, pdfContent);
                    reportService.updateReportScheduler(reportShareSchedule);
                    break;
            }

            //reportShareSchedule.setLastRunDate(LocalDateTime.now());
            //Long runCount = reportShareSchedule.getRunCount();
            //reportShareSchedule.setRunCount(runCount + 1);
            //reportShareSchedule.setEmails("NotEmpty");// quick tweak
            //reportShareScheduleRepository.saveAndFlush(reportShareSchedule);

            LOGGER.info("[event:OFFLINE_REPORT] : schedule id: {} frequency: {} , format: {}",
                    reportShareSchedule.getId(), reportShareSchedule.getReportSharingFrequency(), reportShareSchedule.getReportFormat());
        } catch (Exception e) {
            LOGGER.error("[event:OFFLINE_REPORT] : Unable to send report for scheduler - id: {} reportId: {} ", reportShareSchedule.getId(), reportShareSchedule.getReport().getId(), e);
        }
    }
}
