package com.formreleaf.scheduler.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * @author Bazlur Rahman Rokon
 * @since 10/6/15.
 * <p>
 * ref: https://gist.github.com/rokon12/4b1cfc70eef2394e45d5
 */
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
    private transient AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        final Object jobInstance = super.createJobInstance(bundle);
        beanFactory.autowireBean(jobInstance);

        return jobInstance;
    }
}
