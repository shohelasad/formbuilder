package com.formreleaf.scheduler.service;


import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




/**
 * @author Md. Asaduzzaman
 * @since 10/15/15.
 */
@Service
public class PublishSchedulerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublishSchedulerService.class);

    @Autowired
    private PublishService publishService;
    

    @Transactional
    public void onRefreshPublishInterval() {
        LOGGER.info("[event:OFFLINE_PUBLISH] onRefreshPublishInterval()");
        
        publishService.updatePublishStatus();    
    }
}
