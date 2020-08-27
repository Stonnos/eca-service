package com.ecaservice.load.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Eca load tests config model.
 *
 * @author Roman Batygin
 */
@Data
@Validated
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
    @NotNull
    private Long workerThreadTimeOutInSeconds;

    /**
     * Request processing timeout in seconds
     */
    private Long requestTimeoutInSeconds;

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

    /**
     * Training data storage path
     */
    @NotEmpty
    private String trainingDataStoragePath;

    /**
     * Classifiers options storage path
     */
    @NotEmpty
    private String classifiersStoragePath;

    /**
     * Page size for paging processing
     */
    private Integer pageSize;

    /**
     * Delay in sec. for scheduler
     */
    private Integer delaySeconds;
}
