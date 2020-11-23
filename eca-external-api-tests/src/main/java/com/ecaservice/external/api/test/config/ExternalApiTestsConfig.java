package com.ecaservice.external.api.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
     * Test data path
     */
    @NotEmpty
    private String testDataPath;

    /**
     * Threads number for requests sending
     */
    @NotNull
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
}
