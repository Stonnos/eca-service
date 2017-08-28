package com.ecaservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Eca - service configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class EcaServiceConfiguration {

    /**
     * Creates <tt>ExecutorService</tt> bean
     *
     * @return {@link ExecutorService} bean
     */
    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }
}
