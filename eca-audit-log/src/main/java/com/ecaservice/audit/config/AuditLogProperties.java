package com.ecaservice.audit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Audit log properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("audit")
public class AuditLogProperties {

    /**
     * Rabbit properties
     */
    private RabbitProperties rabbit = new RabbitProperties();

    /**
     * RabbitMQ properties.
     */
    @Data
    public static class RabbitProperties {

        /**
         * Rabbit enabled?
         */
        private boolean enabled;

        /**
         * Queue name to receive audit events
         */
        private String queueName;
    }
}
