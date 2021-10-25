package com.ecaservice.core.mail.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Eca mail client properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("mail.client")
public class EcaMailClientProperties {

    private static final int DEFAULT_THREAD_POOL_SIZE = 1;
    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final long DEFAULT_REDELIVERY_INTERVAL_MILLIS = 60000L;

    /**
     * Is email sending enabled?
     */
    private Boolean enabled;

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
}
