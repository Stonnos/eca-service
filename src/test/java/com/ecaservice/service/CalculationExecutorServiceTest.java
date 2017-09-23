package com.ecaservice.service;

import com.ecaservice.service.impl.CalculationExecutorServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Unit tests that checks CalculationExecutorService functionality
 * (see {@link CalculationExecutorService}).
 *
 * @author Roman Batygin
 */
public class CalculationExecutorServiceTest {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private CalculationExecutorService executorService;

    @Before
    public void setUp() {
        executorService = new CalculationExecutorServiceImpl(executor);
    }

    @Test(expected = TimeoutException.class)
    public void testTimeOut() throws Exception {
        executorService.execute(() -> {
            {
                Thread.sleep(1100);
                return null;
            }
        }, 1000l, TimeUnit.MILLISECONDS);
    }
}

