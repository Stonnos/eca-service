package com.ecaservice.core.lock.config;

import com.ecaservice.core.lock.fallback.DefaultFallbackHandler;
import com.ecaservice.core.lock.fallback.FallbackHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Core lock configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@ComponentScan({"com.ecaservice.core.lock"})
public class CoreLockConfiguration {

    /**
     * Default fallback handler bean.
     */
    public static final String DEFAULT_FALLBACK_HANDLER = "defaultFallbackHandler";

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
