package com.ecaservice.core.lock.config;

import com.ecaservice.core.lock.fallback.DefaultFallbackHandler;
import com.ecaservice.core.lock.fallback.FallbackHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.locks.DefaultLockRegistry;

/**
 * Core lock configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@ComponentScan({"com.ecaservice.core.lock"})
@ConditionalOnProperty(value = "lock.enabled", havingValue = "true")
public class CoreLockConfiguration {

    /**
     * Default fallback handler bean
     */
    public static final String DEFAULT_FALLBACK_HANDLER = "defaultFallbackHandler";

    /**
     * Default lock registry bean
     */
    public static final String DEFAULT_LOCK_REGISTRY = "defaultLockRegistry";

    /**
     * Creates default fallback handler bean.
     *
     * @return default fallback handler bean
     */
    @Bean(DEFAULT_FALLBACK_HANDLER)
    public FallbackHandler fallbackHandler() {
        return new DefaultFallbackHandler();
    }

    /**
     * Creates default lock registry bean.
     *
     * @return default lock registry bean
     */
    @Bean(DEFAULT_LOCK_REGISTRY)
    public DefaultLockRegistry lockRegistry() {
        return new DefaultLockRegistry();
    }
}
