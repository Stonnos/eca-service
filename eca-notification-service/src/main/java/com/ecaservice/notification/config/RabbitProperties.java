package com.ecaservice.notification.config;

import lombok.Data;

/**
 * RabbitMQ properties.
 *
 * @author Roman Batygin
 */
@Data
public class RabbitProperties {

    /**
     * Rabbit enabled?
     */
    private boolean enabled;

    /**
     * Queue name to receive audit events
     */
    private String queueName;
}