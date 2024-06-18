package com.ecaservice.server.config.rabbit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

/**
 * Queue config.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("queue")
public class QueueConfig {

    /**
     * Evaluation request queue name.
     */
    @NotBlank
    private String evaluationRequestQueue;

    /**
     * Evaluation optimizer request queue name.
     */
    @NotBlank
    private String evaluationOptimizerRequestQueue;

    /**
     * Experiment request queue name.
     */
    @NotBlank
    private String experimentRequestQueue;
}
