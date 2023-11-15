package com.ecaservice.core.lock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

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
     * Lock registries properties
     */
    private Map<String, LockRegistryProperties> registries = newHashMap();

    /**
     * Lock registry properties.
     */
    @Data
    public static class LockRegistryProperties {

        private static final long DEFAULT_EXPIRE_AFTER = 60000L;

        /**
         * Lock duration in millis.
         */
        private Long expireAfter = DEFAULT_EXPIRE_AFTER;
    }
}
