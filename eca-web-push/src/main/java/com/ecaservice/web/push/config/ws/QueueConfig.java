package com.ecaservice.web.push.config.ws;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Queues properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("queues")
public class QueueConfig {

    /**
     * Experiment queue
     */
    private String experimentQueue;
}
