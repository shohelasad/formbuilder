package com.formreleaf.scheduler.job;

import com.formreleaf.scheduler.service.ReportSchedulerService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * @author Bazlur Rahman Rokon
 * @since 10/6/15.
 */

@Transactional
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
public class OfflineReportSharingJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineReportSharingJob.class);

    @Value("${jobs.offline.report.enabled}")
    private boolean offlineReportJobEnabled;

    @Autowired
    private ReportSchedulerService reportSchedulerService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        if (offlineReportJobEnabled) {
            LOGGER.info("---------------------------------------------------------");
            LOGGER.info("[event:OFFLINE_REPORT] ReportSharingJob : started : {}", context.getFireTime());
            long startTime = System.nanoTime();

            reportSchedulerService.onRefreshReportShareInterval();
            long endTime = System.nanoTime();

            LOGGER.info("[event:OFFLINE_REPORT] ReportSharingJob : finished. Total Runtime : {}", (endTime - startTime));
            LOGGER.info("---------------------------------------------------------");
        } else {
            LOGGER.info("[event:OFFLINE_REPORT] ReportSharingJob : job is disabled");
        }
    }
}
