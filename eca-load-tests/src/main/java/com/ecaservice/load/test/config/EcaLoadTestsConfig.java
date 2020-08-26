package com.ecaservice.load.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Eca load tests config model.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("eca-load-tests")
public class EcaLoadTestsConfig {

    /**
     * Requests number to eca - server
     */
    private Integer numRequests;

    /**
     * Threads number for requests sending
     */
    private Integer numThreads;

    /**
     * Worker thread timeout in seconds
     */
    private Long workerThreadTimeOutInSeconds;

    /**
     * Folds number for k * V cross - validation method
     */
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    private Integer seed;
}
