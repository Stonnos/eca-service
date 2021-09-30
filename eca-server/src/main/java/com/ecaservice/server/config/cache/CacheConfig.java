package com.ecaservice.server.config.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Cache config.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("cache")
public class CacheConfig {

    /**
     * Cache specifications map
     */
    private Map<String, CacheSpec> specs;
}
