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
}
