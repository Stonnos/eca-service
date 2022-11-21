package com.ecaservice.external.api.config.rabbit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Queue config model.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("queue")
public class QueueConfig {

    /**
     * Evaluation request queue name
     */
    private String evaluationRequestQueue;

    /**
     * Optimal evaluation request queue name
     */
    private String optimalEvaluationRequestQueue;

    /**
     * Experiment request queue name
     */
    private String experimentRequestQueue;

    /**
     * Evaluation response queue name
     */
    private String evaluationResponseQueue;

    /**
     * Experiment response queue name
     */
    private String experimentResponseQueue;
}
