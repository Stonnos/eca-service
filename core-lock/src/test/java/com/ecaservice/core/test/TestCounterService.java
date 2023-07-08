package com.ecaservice.core.test;

import com.ecaservice.core.lock.annotation.Locked;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class TestCounterService {

    private static final long DELAY = 200L;

    private Map<String, Integer> counters = newHashMap();

    @Locked(lockName = "counter", key = "#key")
    public void incrementBy(String key, int value) {
        Integer current = counters.getOrDefault(key, 0);
        current = current + value;
        counters.put(key, current);
    }

    @Locked(lockName = "counter", key = "#key", waitForLock = false)
    public void tryIncrement(String key, int value) throws InterruptedException {
        Thread.sleep(DELAY);
        counters.put(key, value);
    }

    public void clear() {
        counters.clear();
    }

    public Integer get(String key) {
        return counters.getOrDefault(key, 0);
    }
}
