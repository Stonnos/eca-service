package com.ecaservice.core.test;

import com.ecaservice.core.lock.annotation.Locked;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.ecaservice.core.config.TestLockConfiguration.DEFAULT_LOCK_REGISTRY;
import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class TestCounterService {

    private Map<String, Integer> counters = newHashMap();

    @Locked(lockName = "counter", key = "#key", lockRegistry = DEFAULT_LOCK_REGISTRY)
    public void incrementBy(String key, int value) {
        Integer current = counters.getOrDefault(key, 0);
        current = current + value;
        counters.put(key, current);
    }

    public Integer get(String key) {
        return counters.getOrDefault(key, 0);
    }
}
