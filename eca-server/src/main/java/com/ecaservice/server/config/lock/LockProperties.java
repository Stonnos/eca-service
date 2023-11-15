package com.ecaservice.server.config.lock;

import lombok.Data;

/**
 * Lock properties.
 *
 * @author Roman Batygin
 */
@Data
public class LockProperties {

    /**
     * Registry key
     */
    private String registryKey;

    /**
     * Lock duration in millis.
     */
    private Long expireAfter;
}
