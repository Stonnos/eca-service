package com.ecaservice.core.lock.config;

import com.ecaservice.core.lock.fallback.DefaultFallbackHandler;
import com.ecaservice.core.lock.fallback.FallbackHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Core lock configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(LockProperties.class)
@ComponentScan({"com.ecaservice.core.lock"})
@ConditionalOnProperty(value = "lock.enabled", havingValue = "true")
public class CoreLockAutoConfiguration {

    /**
     * Default fallback handler bean
     */
    public static final String DEFAULT_FALLBACK_HANDLER = "defaultFallbackHandler";

    /**
     * Default lock registry key
     */
    public static final String DEFAULT_LOCK_REGISTRY_KEY = "defaultLockRegistryKey";

    /**
     * Creates default fallback handler bean.
     *
     * @return default fallback handler bean
     */
    @Bean(DEFAULT_FALLBACK_HANDLER)
    public FallbackHandler fallbackHandler() {
        return new DefaultFallbackHandler();
    }
}
