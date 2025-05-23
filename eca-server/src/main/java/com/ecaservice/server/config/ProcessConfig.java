package com.ecaservice.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;

/**
 * BPMN process config.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("processes")
public class ProcessConfig {

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
     * Create evaluation request process id
     */
    @NotEmpty
    private String createEvaluationRequestProcessId;

    /**
     * Cancel experiment process id
     */
    @NotEmpty
    private String cancelExperimentProcessId;
}
