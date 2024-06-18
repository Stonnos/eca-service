package com.ecaservice.core.audit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Audit properties model.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("audit")
public class AuditProperties {

    private static final int DEFAULT_THREAD_POOL_SIZE = 1;

    /**
     * Is audit enabled?
     */
    private Boolean enabled;

    /**
     * Use async events?
     */
    private Boolean asyncEvents;

    /**
     * Thread pool size
     */
    private Integer threadPoolSize = DEFAULT_THREAD_POOL_SIZE;

    /**
     * Sender properties
     */
    private SenderProperties sender = new SenderProperties();

    /**
     * Rabbitmq properties
     */
    private RabbitProperties rabbit = new RabbitProperties();

    /**
     * RabbitMQ properties.
     */
    @Data
    public static class RabbitProperties {

        private static final String DEFAULT_QUEUE_NAME = "queue-audit-events";

        /**
         * Queue name to sent audit events
         */
        private String queueName = DEFAULT_QUEUE_NAME;
    }

    /**
     * Sender properties.
     */
    @Data
    public static class SenderProperties {

        /**
         * Sender type
         */
        private SenderType type = SenderType.WEB;

    }
}
