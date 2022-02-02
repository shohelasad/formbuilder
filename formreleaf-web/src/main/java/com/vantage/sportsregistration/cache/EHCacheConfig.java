package com.vantage.sportsregistration.cache;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import com.vantage.sportsregistration.exceptions.InitializationException;

import net.sf.ehcache.config.CacheConfiguration;



/**
 * Cache configuration if user wants to go with Ehcache
 */
//@Configuration
//@EnableCaching
public class EHCacheConfig //implements CachingConfigurer /*, CacheConfig<net.sf.ehcache.CacheManager> */ 
{

    /*@Override
    public FactoryBean<net.sf.ehcache.CacheManager> getFactoryBean() {
        final EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        factoryBean.setCacheManagerName("ehcache.name");
        factoryBean.setShared(true);
        return factoryBean;
    }

    @Override
    public CacheManager getCacheManager() {
        try {
            return new EhCacheCacheManager(getFactoryBean().getObject());
        } catch (Exception e) {
            throw new InitializationException("Exception while creating EHCache Manager", e);
        }
    }*/
    
    /*@Bean(destroyMethod="shutdown")
    public net.sf.ehcache.CacheManager ehCacheManager() {
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("EhCache");
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.setMaxEntriesLocalHeap(1000);

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(cacheConfiguration);

        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean
    @Override
    public CacheManager getCacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }*/

    @Bean
    //@Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }
    

	//@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        return ehCacheManagerFactoryBean;
    }
	
   // @Bean
    public CacheManager cacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
        return cacheManager;
    }

    /*@Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }*/

	/*@Override
	public CacheResolver cacheResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CacheErrorHandler errorHandler() {
		// TODO Auto-generated method stub
		return null;
	}
*/
	
}
