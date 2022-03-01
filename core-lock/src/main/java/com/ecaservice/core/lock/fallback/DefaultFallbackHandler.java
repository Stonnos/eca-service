package com.ecaservice.core.lock.fallback;

import lombok.extern.slf4j.Slf4j;

/**
 * Default fallback handler.
 *
 * @author Roman Batygin
 */
@Slf4j
public class DefaultFallbackHandler implements FallbackHandler {

    @Override
    public void fallback(String lockKey) {
        log.info("Can't not acquire lock for key [{}]. Skip...", lockKey);
    }
}
