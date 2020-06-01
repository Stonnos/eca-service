package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for checking {@link ExperimentResultsLockService} functionality.
 *
 * @author Roman Batygin
 */
class ExperimentResultsLockServiceTest {

    private ExperimentResultsLockService experimentResultsLockService = new ExperimentResultsLockService();

    @Test
    void testLock() {
        experimentResultsLockService.lock(TestHelperUtils.TEST_UUID);
        Assertions.assertThat(experimentResultsLockService.locked(TestHelperUtils.TEST_UUID)).isTrue();
    }

    @Test
    void testUnlock() {
        experimentResultsLockService.lock(TestHelperUtils.TEST_UUID);
        experimentResultsLockService.unlock(TestHelperUtils.TEST_UUID);
        Assertions.assertThat(experimentResultsLockService.locked(TestHelperUtils.TEST_UUID)).isFalse();
    }
}
