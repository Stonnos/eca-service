package com.ecaservice.core.lock.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Redis locks properties.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("redis-lock")
public class RedisLockProperties {

    private static final long DEFAULT_EXPIRE_AFTER = 60000L;
    private static final String DEFAULT_REGISTRY_KEY = "lock";

    /**
     * Registry key.
     */
    @NotNull
    private String registryKey = DEFAULT_REGISTRY_KEY;

    /**
     * Lock duration in millis.
     */
    @NotNull
    private Long expireAfter = DEFAULT_EXPIRE_AFTER;
}
