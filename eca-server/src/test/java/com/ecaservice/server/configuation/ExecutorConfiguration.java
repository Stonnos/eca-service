package com.ecaservice.server.configuation;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executor service configuration.
 *
 * @author Roman Batygin
 */
@TestConfiguration
public class ExecutorConfiguration {

    /**
     * Creates executor service bean.
     *
     * @return executor service bean
     */
    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }
}
