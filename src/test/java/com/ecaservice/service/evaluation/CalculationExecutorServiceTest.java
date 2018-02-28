package com.ecaservice.service.evaluation;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Unit tests that checks CalculationExecutorService functionality {@see CalculationExecutorService}.
 *
 * @author Roman Batygin
 */
public class CalculationExecutorServiceTest {

    private static final long TIMEOUT = 1000L;

    private CalculationExecutorService executorService;

    @Before
    public void setUp() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executorService = new CalculationExecutorServiceImpl(executor);
    }

    @Test(expected = TimeoutException.class)
    public void testTimeOut() throws Exception {
        executorService.execute(() -> {
            {
                Thread.sleep(TIMEOUT + 100L);
                return null;
            }
        }, TIMEOUT, TimeUnit.MILLISECONDS);
    }
}

