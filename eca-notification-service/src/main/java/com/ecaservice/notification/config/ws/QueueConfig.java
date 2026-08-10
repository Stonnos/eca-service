package com.ecaservice.notification.config.ws;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Queues properties.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("queues")
public class QueueConfig {

    /**
     * Push queue
     */
    @NotEmpty
    private String pushQueue;
}
