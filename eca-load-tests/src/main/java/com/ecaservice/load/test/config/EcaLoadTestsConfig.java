package com.ecaservice.load.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_FOLDS;
import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_REQUESTS;
import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_TESTS;
import static com.ecaservice.load.test.dto.FieldConstraints.MAX_NUM_THREADS;
import static com.ecaservice.load.test.dto.FieldConstraints.MIN_NUM_FOLDS;
import static com.ecaservice.load.test.dto.FieldConstraints.MIN_NUM_REQUESTS;
import static com.ecaservice.load.test.dto.FieldConstraints.MIN_NUM_TESTS;
import static com.ecaservice.load.test.dto.FieldConstraints.MIN_NUM_THREADS;

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
    @NotNull
    @Min(MIN_NUM_REQUESTS)
    @Max(MAX_NUM_REQUESTS)
    private Integer numRequests;

    /**
     * Threads number for requests sending
     */
    @NotNull
    @Min(MIN_NUM_THREADS)
    @Max(MAX_NUM_THREADS)
    private Integer numThreads;

    /**
     * Worker thread timeout in seconds
     */
    @NotNull
    private Long workerThreadTimeOutInSeconds;

    /**
     * Request processing timeout in seconds
     */
    @NotNull
    private Long requestTimeoutInSeconds;

    /**
     * Folds number for k * V cross - validation method
     */
    @NotNull
    @Min(MIN_NUM_FOLDS)
    @Max(MAX_NUM_FOLDS)
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @NotNull
    @Min(MIN_NUM_TESTS)
    @Max(MAX_NUM_TESTS)
    private Integer numTests;

    /**
     * Seed value for random generator
     */
    @NotNull
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
    @NotNull
    private Integer pageSize;

    /**
     * Delay in sec. for scheduler
     */
    @NotNull
    private Integer delaySeconds;
}
