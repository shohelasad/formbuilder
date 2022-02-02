package com.vantage.sportsregistration.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import net.sf.ehcache.config.CacheConfiguration;



//@Configuration
//@EnableCaching
//@Component
public class CachingConfig /*extends CachingConfigurerSupport */{
	
   // @Bean(destroyMethod="shutdown")
    public net.sf.ehcache.CacheManager ehCacheManager() {
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("userCache");
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.setMaxEntriesLocalHeap(1000);

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(cacheConfiguration);

        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    /*@Bean
    @Override
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

	@Override
	public CacheResolver cacheResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CacheErrorHandler errorHandler() {
		// TODO Auto-generated method stub
		return null;
	}*/
}
