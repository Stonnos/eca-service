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
    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final long DEFAULT_REDELIVERY_INTERVAL_MILLIS = 60000L;
    private static final String DEFAULT_REGISTRY = "defaultRegistry";
    private static final long DEFAULT_EXPIRE_AFTER = 60000L;

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
     * Page size (used for pagination)
     */
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * Enabled redelivery?
     */
    private Boolean redelivery;

    /**
     * Redelivery interval in millis
     */
    private Long redeliveryIntervalMillis = DEFAULT_REDELIVERY_INTERVAL_MILLIS;

    /**
     * Lock config
     */
    private LockProperties lock = new LockProperties();

    /**
     * Lock properties
     */
    @Data
    public static class LockProperties {

        /**
         * Registry key
         */
        private String registryKey = DEFAULT_REGISTRY;

        /**
         * Lock duration in millis.
         */
        private Long expireAfter = DEFAULT_EXPIRE_AFTER;
    }
}
