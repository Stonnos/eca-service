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
}
