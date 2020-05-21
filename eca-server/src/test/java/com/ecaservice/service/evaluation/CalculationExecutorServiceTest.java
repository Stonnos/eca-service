package com.ecaservice.service.evaluation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests that checks CalculationExecutorService functionality {@see CalculationExecutorService}.
 *
 * @author Roman Batygin
 */
public class CalculationExecutorServiceTest {

    private static final long TIMEOUT = 1000L;

    private CalculationExecutorService executorService;

    @BeforeEach
    void setUp() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executorService = new CalculationExecutorServiceImpl(executor);
    }

    @Test
    void testTimeOut() {
        assertThrows(TimeoutException.class, () ->
                executorService.execute(() -> {
                    Thread.sleep(TIMEOUT + 100L);
                    return null;
                }, TIMEOUT, TimeUnit.MILLISECONDS));
    }
}

