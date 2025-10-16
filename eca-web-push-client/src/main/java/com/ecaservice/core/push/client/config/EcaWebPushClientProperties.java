package com.ecaservice.core.push.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Eca web push client properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("web-push.client")
public class EcaWebPushClientProperties {

    private static final int DEFAULT_THREAD_POOL_SIZE = 1;

    /**
     * Is web push sending enabled?
     */
    private Boolean enabled;

    /**
     * Use async sending?
     */
    private Boolean async;

    /**
     * Thread pool size
     */
    private Integer threadPoolSize = DEFAULT_THREAD_POOL_SIZE;

    /**
     * Rabbitmq properties
     */
    private RabbitProperties rabbit = new RabbitProperties();

    /**
     * RabbitMQ properties.
     */
    @Data
    public static class RabbitProperties {

        private static final String DEFAULT_QUEUE_NAME = "queue-push-events";

        /**
         * Queue name to sent push events
         */
        private String queueName = DEFAULT_QUEUE_NAME;
    }
}
