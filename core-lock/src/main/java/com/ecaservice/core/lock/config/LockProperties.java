package com.ecaservice.core.lock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Lock properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("lock")
public class LockProperties {

    /**
     * Locks enabled?
     */
    private boolean enabled;

    /**
     * Lock registry type
     */
    private LockRegistryType registryType = LockRegistryType.IN_MEMORY;

    /**
     * Redis lock properties
     */
    private RedisLockProperties redis = new RedisLockProperties();

    /**
     * Redis lock properties.
     */
    @Data
    public static class RedisLockProperties {

        private static final long DEFAULT_EXPIRE_AFTER = 60000L;
        private static final String DEFAULT_REGISTRY_KEY = "lock";

        /**
         * Registry key.
         */
        private String registryKey = DEFAULT_REGISTRY_KEY;

        /**
         * Lock duration in millis.
         */
        private Long expireAfter = DEFAULT_EXPIRE_AFTER;
    }
}
