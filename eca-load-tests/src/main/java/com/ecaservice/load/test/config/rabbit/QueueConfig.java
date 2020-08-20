package com.ecaservice.load.test.config.rabbit;

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
     * Response queue name
     */
    private String replyToQueue;
}
