package com.ecaservice.core.transactional.outbox.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Transactional outbox properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("transactional.outbox")
public class TransactionalOutboxProperties {

    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final int DEFAULT_LOCK_TTL_SECONDS = 60;
    private static final long DEFAULT_RETRY_INTERVAL_MILLIS = 60000L;

    /**
     * Retry outbox messages enabled?
     */
    private boolean retry = true;

    /**
     * Page size (used for pagination)
     */
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * Scheduler retry interval in millis
     */
    private Long retryIntervalMillis = DEFAULT_RETRY_INTERVAL_MILLIS;

    /**
     * Lock ttl in seconds
     */
    private Integer lockTtlSeconds = DEFAULT_LOCK_TTL_SECONDS;
}
