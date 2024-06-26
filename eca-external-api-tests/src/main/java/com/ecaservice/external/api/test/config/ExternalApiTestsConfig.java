package com.ecaservice.external.api.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import static com.ecaservice.external.api.test.util.Constraints.MAX_NUM_THREADS;
import static com.ecaservice.external.api.test.util.Constraints.MIN_NUM_THREADS;

/**
 * External API tests config.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("external-api-tests")
public class ExternalApiTestsConfig {

    /**
     * External api url
     */
    @NotEmpty
    private String url;

    /**
     * Evaluation requests test data path
     */
    @NotEmpty
    private String evaluationTestDataPath;

    /**
     * Experiment requests test data path
     */
    @NotEmpty
    private String experimentTestDataPath;

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
    private Long workerThreadTimeoutInSeconds;

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

    /**
     * Experiment models number in experiment history
     */
    @NotNull
    private Integer expectedExperimentNumModels;
}
