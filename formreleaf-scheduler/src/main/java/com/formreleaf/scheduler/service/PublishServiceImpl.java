package com.formreleaf.scheduler.service;

import com.formreleaf.domain.Publish;
import com.formreleaf.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Md. Asaduzzaman
 * @since 9/28/15.
 */
@Service
@Transactional
public class PublishServiceImpl implements PublishService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublishServiceImpl.class);

    @Autowired
    private PublishRepository publishRepository;


    @Override
    public void updatePublishStatus() {
    	LOGGER.info("[event:OFFLINE_PUBLISH] updating publish status");
    	
        publishRepository.setPublishStatusToOpen();
        publishRepository.setPublishStatusToClosed();
        publishRepository.setPublishStatusToPublic();
        publishRepository.setPublishStatusToPrivate();
    }

}
