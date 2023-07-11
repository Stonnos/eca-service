package com.ecaservice.core.config;

import com.ecaservice.core.lock.fallback.DefaultFallbackHandler;
import com.ecaservice.core.test.TestCounterService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

@TestConfiguration
public class TestLockConfiguration {

    @Bean
    public TestCounterService testCounterService() {
        return new TestCounterService();
    }

    @Bean
    public LockRegistry defaultLockRegistry() {
        return new DefaultLockRegistry();
    }

    @Bean
    public DefaultFallbackHandler defaultFallbackHandler() {
        return new DefaultFallbackHandler();
    }
}
