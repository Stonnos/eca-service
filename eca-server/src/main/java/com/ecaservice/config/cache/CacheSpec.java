package com.ecaservice.config.cache;

import lombok.Data;

/**
 * Cache specification config.
 *
 * @author Roman Batygin
 */
@Data
public class CacheSpec {

    /**
     * Expires after write in seconds
     */
    private Long expireAfterWriteSeconds;

    /**
     * Cache max size
     */
    private Long maxSize;
}