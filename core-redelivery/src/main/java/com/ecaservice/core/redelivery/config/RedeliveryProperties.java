package com.ecaservice.core.redelivery.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redelivery properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("redelivery")
public class RedeliveryProperties {

    private static final int DEFAULT_THREAD_POOL_SIZE = 1;
    private static final long DEFAULT_REDELIVERY_INTERVAL_MILLIS = 60000L;
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final int DEFAULT_MAX_REQUESTS = 50;

    /**
     * Is redelivery lib enabled?
     */
    private Boolean enabled;

    /**
     * Page size (used for pagination)
     */
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * Maximum requests to process per one job (used for throttling)
     */
    private Integer maxRequests = DEFAULT_MAX_REQUESTS;

    /**
     * Thread pool size
     */
    private Integer threadPoolSize = DEFAULT_THREAD_POOL_SIZE;

    /**
     * Scheduler interval in millis
     */
    private Long redeliveryIntervalMillis = DEFAULT_REDELIVERY_INTERVAL_MILLIS;
}
