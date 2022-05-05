package com.ecaservice.core.aspect;

import com.ecaservice.core.config.TestLockConfiguration;
import com.ecaservice.core.lock.aspect.LockExecutionAspect;
import com.ecaservice.core.lock.service.LockMeterService;
import com.ecaservice.core.test.TestCounterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link LockExecutionAspect} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@EnableAspectJAutoProxy
@Import({LockExecutionAspect.class, TestLockConfiguration.class})
class LockExecutionAspectTest {

    private static final String KEY = "key";
    private static final int VALUE = 4;
    private static final int NUM_THREADS = 4;
    private static final int NUM_ITS = 100;

    @MockBean
    private LockMeterService lockMeterService;

    @Inject
    private TestCounterService testCounterService;

    @BeforeEach
    void init() {
        testCounterService.clear();
    }

    @Test
    void testLock() throws InterruptedException {
        final CountDownLatch finishedLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < NUM_ITS; j++) {
                        testCounterService.incrementBy(KEY, VALUE);
                    }
                } finally {
                    finishedLatch.countDown();
                }
            });
        }
        finishedLatch.await();
        executorService.shutdownNow();
        assertThat(testCounterService.get(KEY)).isEqualTo(NUM_THREADS * VALUE * NUM_ITS);
    }

    @Test
    void testTryLock() throws InterruptedException {
        final CountDownLatch finishedLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                   testCounterService.tryIncrement(KEY, VALUE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishedLatch.countDown();
                }
            });
        }
        finishedLatch.await();
        executorService.shutdownNow();
        assertThat(testCounterService.get(KEY)).isEqualTo(VALUE);
    }
}
