package com.ecaservice.auto.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Auto tests properties.
 *
 * @author Roman Batygin
 */
@Validated
@Data
@ConfigurationProperties("auto-tests")
public class AutoTestsProperties {

    private static final int MIN_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 10;

    /**
     * Experiments test data path
     */
    @NotEmpty
    private String experimentsDataPath;

    /**
     * Classifiers data path
     */
    @NotEmpty
    private String classifiersDataPath;

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
     * Scheduler pool size
     */
    @NotNull
    @Min(MIN_POOL_SIZE)
    @Max(MAX_POOL_SIZE)
    private Integer schedulerPoolSize;

    /**
     * Request processing timeout in seconds
     */
    @NotNull
    private Long requestTimeoutInSeconds;

    /**
     * Eca ers base url
     */
    @NotEmpty
    private String ecaErsBaseUrl;
}
