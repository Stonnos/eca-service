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
     * Process experiment process id
     */
    @NotEmpty
    private String processExperimentId;

    /**
     * Process evaluation process id
     */
    @NotEmpty
    private String processEvaluationId;

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
     * Create evaluation web request process id
     */
    @NotEmpty
    private String createEvaluationWebRequestProcessId;

    /**
     * Max. concurrent experiment processes
     */
    private int maxConcurrentExperimentProcesses = DEFAULT_MAX_CONCURRENT_EXPERIMENT_PROCESSES;
}
