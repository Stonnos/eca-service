package com.ecaservice.auto.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

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

    /**
     * Experiments test data path
     */
    @NotEmpty
    private String experimentsDataPath;

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
     * Request processing timeout in seconds
     */
    @NotNull
    private Long requestTimeoutInSeconds;
}
