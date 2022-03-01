package com.ecaservice.core.config;

import com.ecaservice.core.test.TestCounterService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

@TestConfiguration
public class TestLockConfiguration {

    public static final String DEFAULT_LOCK_REGISTRY = "defaultLockRegistry";

    @Bean
    public TestCounterService testCounterService() {
        return new TestCounterService();
    }

    @Bean(DEFAULT_LOCK_REGISTRY)
    public LockRegistry defaultLockRegistry() {
        return new DefaultLockRegistry();
    }
}
