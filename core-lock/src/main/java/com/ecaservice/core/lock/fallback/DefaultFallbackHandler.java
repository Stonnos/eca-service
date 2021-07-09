package com.ecaservice.core.lock.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default fallback handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class DefaultFallbackHandler implements FallbackHandler {

    @Override
    public void apply() {
        log.info("Can;t not acquire lock for key. Skip...");
    }
}
