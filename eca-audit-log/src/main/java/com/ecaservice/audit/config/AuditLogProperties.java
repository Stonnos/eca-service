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

    private static final int DEFAULT_MAXIMUM_PAGES_NUM = 99;

    /**
     * Maximum pages number
     */
    private Integer maxPagesNum = DEFAULT_MAXIMUM_PAGES_NUM;

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
