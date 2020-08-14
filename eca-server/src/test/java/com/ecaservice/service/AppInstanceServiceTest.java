package com.ecaservice.service;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.repository.AppInstanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AppInstanceService} functionality.
 *
 * @author Roman Batygin
 */
@Import({AppInstanceService.class, CommonConfig.class})
class AppInstanceServiceTest extends AbstractJpaTest {

    private static final int NUM_THREADS = 2;

    @Inject
    private AppInstanceService appInstanceService;
    @Inject
    private AppInstanceRepository appInstanceRepository;

    @Override
    public void deleteAll() {
        appInstanceRepository.deleteAll();
    }

    @Test
    void testAppInstanceSavingInMultiThreadMode() throws InterruptedException {
        final CountDownLatch finishedLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                appInstanceService.getOrSaveAppInstance();
                finishedLatch.countDown();
            });
        }
        finishedLatch.await();
        executorService.shutdownNow();
        assertThat(appInstanceRepository.count()).isOne();
    }
}
