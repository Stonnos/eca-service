package com.ecaservice.web.push.config.ws;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

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
