package com.ecaservice.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * BPMN process config.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("processes")
public class ProcessConfig {

    private static final int DEFAULT_MAX_CONCURRENT_EXPERIMENT_PROCESSES = 1;

    /**
     * Process experiment diagram id
     */
    @NotEmpty
    private String processExperimentId;

    /**
     * Create experiment request process id
     */
    @NotEmpty
    private String createExperimentRequestProcessId;

    /**
     * Create experiment web request process id
     */
    @NotEmpty
    private String createExperimentWebRequestProcessId;

    /**
     * Max. concurrent experiment processes
     */
    private int maxConcurrentExperimentProcesses = DEFAULT_MAX_CONCURRENT_EXPERIMENT_PROCESSES;
}
