package com.formreleaf.scheduler.job;

import com.formreleaf.scheduler.service.PublishSchedulerService;
import com.formreleaf.scheduler.service.PublishService;
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
 * @author Md. Asauzzaman
 * @since 10/6/15.
 */

@Transactional
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component
public class OfflinePublishUpdateJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfflinePublishUpdateJob.class);

    @Value("${jobs.offline.publish.enabled}")
    private boolean offlinePublishJobEnabled;

    @Autowired
    private PublishSchedulerService publishSchedulerService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        if (offlinePublishJobEnabled) {
            LOGGER.info("---------------------------------------------------------");
            LOGGER.info("[event:OFFLINE_PUBLISH] PublishingJob : started : {}", context.getFireTime());
            long startTime = System.nanoTime();

            publishSchedulerService.onRefreshPublishInterval();
            long endTime = System.nanoTime();

            LOGGER.info("[event:OFFLINE_PUBLISH] PublishingJob : finished. Total Runtime : {}", (endTime - startTime));
            LOGGER.info("---------------------------------------------------------");
        } else {
            LOGGER.info("[event:OFFLINE_PUBLISH] PublishingJob : job is disabled");
        }
    }
}
