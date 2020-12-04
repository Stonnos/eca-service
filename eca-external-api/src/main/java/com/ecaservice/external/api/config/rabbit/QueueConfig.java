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
     * Evaluation request reply to queue name
     */
    private String evaluationRequestReplyToQueue;
}
