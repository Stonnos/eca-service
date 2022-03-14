package com.ecaservice.auto.test.config.rabbit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Queue config model.
 *
 * @author Roman Batygin
 */
@Validated
@Data
@ConfigurationProperties("queue")
public class QueueConfig {

    /**
     * Experiment request queue name
     */
    @NotEmpty
    private String experimentRequestQueue;

    /**
     * Experiment response queue name
     */
    @NotEmpty
    private String experimentReplyToQueue;

    /**
     * Evaluation request queue name
     */
    @NotEmpty
    private String evaluationRequestQueue;

    /**
     * Evaluation response queue name
     */
    @NotEmpty
    private String evaluationReplyToQueue;
}
