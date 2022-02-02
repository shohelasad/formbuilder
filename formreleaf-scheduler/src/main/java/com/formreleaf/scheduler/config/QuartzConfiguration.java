package com.formreleaf.scheduler.config;

import com.formreleaf.scheduler.job.OfflinePublishUpdateJob;
import com.formreleaf.scheduler.job.OfflineReportShareExpiringJob;
import com.formreleaf.scheduler.job.OfflineReportSharingJob;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Bazlur Rahman Rokon
 * @since 10/6/15.
 */

@Configuration
@ComponentScan("com.formreleaf.scheduler")
public class QuartzConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzConfiguration.class);

    public static final String OFFLINE_REPORT_SHARE_EXPIRING_JOB_DETAILS = "offlineReportShareExpiringJobDetails";
    public static final String OFFLINE_REPORT_SHARE_JOB_DETAILS = "offlineReportShareJobDetails";
    public static final String OFFLINE_REPORT_SHARE_JOB_DETAILS_GROUP = "offlineReportShareJobDetailsGroup";

    public static final String OFFLINE_REPORT_SHARE_TRIGGER = "offlineReportShareTrigger";
    public static final String OFFLINE_REPORT_SHARE_TRIGGER_GROUP = "offlineReportShareTriggerGroup";

    public static final String OFFLINE_REPORT_SHARE_EXPIRE_TRIGGER = "offlineReportShareExpireTrigger";
    private static final String OFFLINE_REPORT_SHARE_EXPIRE_TRIGGER_GROUP = "offlineReportShareExpireTriggerGroup";

    public static final String OFFLINE_PUBLISH_UPDATE_JOB_DETAILS = "offlinePublishUpdateJobDetails";
    public static final String OFFLINE_PUBLISH_UPDATE_JOB_DETAILS_GROUP = "offlinePublishUpdateJobDetailsGroup";
    
    public static final String OFFLINE_PUBLISH_UPDATE_TRIGGER = "offlinePublishUpdateTrigger";
    public static final String OFFLINE_PUBLISH_UPDATE_TRIGGER_GROUP = "offlinePublishUpdateTriggerGroup";

    
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${jobs.offline.report.share.trigger.cron}")
    private String offlineReportShareCronExpression;

    @Value("${jobs.offline.report.expire.trigger.cron}")
    private String offlineReportExpireCronExpression;
    
    @Value("${jobs.offline.publish.update.trigger.cron}")
    private String offlinePublishUpdateCronExpression;

    @Value("${jobs.offline.report.startup.delay}")
    private long startupDelay; 

    
    @PostConstruct
    public void init() {
        LOGGER.info("[event:QUARTZ_CONFIG] QuartzConfig initialized.");
        LOGGER.info("[event:QUARTZ_CONFIG] offlineReportShareCronExpression: {}.", offlineReportShareCronExpression);
        LOGGER.info("[event:QUARTZ_CONFIG] offlineReportExpireCronExpression: {} .", offlineReportExpireCronExpression);
        LOGGER.info("[event:QUARTZ_CONFIG] offlinePublishUpdateCronExpression: {} .", offlinePublishUpdateCronExpression);
        LOGGER.info("[event:QUARTZ_CONFIG] Initial startup Delay: {} .", startupDelay);
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean quartzScheduler = new SchedulerFactoryBean();
        quartzScheduler.setTransactionManager(transactionManager);
        quartzScheduler.setDataSource(dataSource);
        quartzScheduler.setOverwriteExistingJobs(true);
        quartzScheduler.setSchedulerName("formreleaf-quartz-scheduler");

        // custom job factory of spring with DI support for @Autowired!
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        quartzScheduler.setJobFactory(jobFactory);

        Trigger[] triggers = {offlineReportShareCronTriggerFactoryBean().getObject(),
                offlineReportShareExpireCronTriggerFactoryBean().getObject(), offlinePublishUpdateCronTriggerFactoryBean().getObject()};
        quartzScheduler.setTriggers(triggers);

        quartzScheduler.setQuartzProperties(quartzProperties());

        return quartzScheduler;
    }

    @Bean
    public JobDetailFactoryBean offlineReportShareExpiringJobDetails() {
        final JobDetailFactoryBean jobDetail = new org.springframework.scheduling.quartz.JobDetailFactoryBean();
        jobDetail.setJobClass(OfflineReportShareExpiringJob.class);
        jobDetail.setName(OFFLINE_REPORT_SHARE_EXPIRING_JOB_DETAILS);
        jobDetail.setGroup(OFFLINE_REPORT_SHARE_JOB_DETAILS_GROUP);
        jobDetail.setDurability(true);

        return jobDetail;
    }

    @Bean
    public JobDetailFactoryBean offlineReportShareJobDetails() {
        final JobDetailFactoryBean jobDetail = new org.springframework.scheduling.quartz.JobDetailFactoryBean();
        jobDetail.setJobClass(OfflineReportSharingJob.class);
        jobDetail.setName(OFFLINE_REPORT_SHARE_JOB_DETAILS);
        jobDetail.setGroup(OFFLINE_REPORT_SHARE_JOB_DETAILS_GROUP);
        jobDetail.setDurability(true);

        return jobDetail;
    }
    
    @Bean
    public CronTriggerFactoryBean offlineReportShareCronTriggerFactoryBean() {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(offlineReportShareJobDetails().getObject());
        factoryBean.setStartDelay(startupDelay);
        factoryBean.setName(OFFLINE_REPORT_SHARE_TRIGGER);
        factoryBean.setGroup(OFFLINE_REPORT_SHARE_TRIGGER_GROUP);
        factoryBean.setCronExpression(offlineReportShareCronExpression);
        factoryBean.setTimeZone(TimeZone.getTimeZone("US/Eastern"));

        return factoryBean;
    }

    @Bean
    public CronTriggerFactoryBean offlineReportShareExpireCronTriggerFactoryBean() {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(offlineReportShareExpiringJobDetails().getObject());
        factoryBean.setStartDelay(startupDelay);
        factoryBean.setName(OFFLINE_REPORT_SHARE_EXPIRE_TRIGGER);
        factoryBean.setGroup(OFFLINE_REPORT_SHARE_EXPIRE_TRIGGER_GROUP);
        factoryBean.setCronExpression(offlineReportExpireCronExpression);
        factoryBean.setTimeZone(TimeZone.getTimeZone("US/Eastern"));

        return factoryBean;
    }
    
    @Bean
    public JobDetailFactoryBean offlinePublishUpdateJobDetails() {
        final JobDetailFactoryBean jobDetail = new org.springframework.scheduling.quartz.JobDetailFactoryBean();
        jobDetail.setJobClass(OfflinePublishUpdateJob.class);
        jobDetail.setName(OFFLINE_PUBLISH_UPDATE_JOB_DETAILS);
        jobDetail.setGroup(OFFLINE_PUBLISH_UPDATE_JOB_DETAILS_GROUP);
        jobDetail.setDurability(true);

        return jobDetail;
    }
    
    @Bean
    public CronTriggerFactoryBean offlinePublishUpdateCronTriggerFactoryBean() {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(offlinePublishUpdateJobDetails().getObject());
        factoryBean.setStartDelay(startupDelay);
        factoryBean.setName(OFFLINE_PUBLISH_UPDATE_TRIGGER);
        factoryBean.setGroup(OFFLINE_PUBLISH_UPDATE_TRIGGER_GROUP);
        factoryBean.setCronExpression(offlinePublishUpdateCronExpression);
        factoryBean.setTimeZone(TimeZone.getTimeZone("US/Eastern"));

        return factoryBean;
    }

    @Bean
    public Properties quartzProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        Properties properties = null;

        try {
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();

        } catch (IOException e) {
            LOGGER.warn("Cannot load quartz.properties.");
        }

        return properties;
    }
}
