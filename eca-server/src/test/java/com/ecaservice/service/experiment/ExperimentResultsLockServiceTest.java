package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Unit tests for checking {@link ExperimentResultsLockService} functionality.
 *
 * @author Roman Batygin
 */
public class ExperimentResultsLockServiceTest {

    private ExperimentResultsLockService experimentResultsLockService = new ExperimentResultsLockService();

    @Test
    public void testLock() {
        experimentResultsLockService.lock(TestHelperUtils.TEST_UUID);
        Assertions.assertThat(experimentResultsLockService.locked(TestHelperUtils.TEST_UUID)).isTrue();
    }

    @Test
    public void testUnlock() {
        experimentResultsLockService.lock(TestHelperUtils.TEST_UUID);
        experimentResultsLockService.unlock(TestHelperUtils.TEST_UUID);
        Assertions.assertThat(experimentResultsLockService.locked(TestHelperUtils.TEST_UUID)).isFalse();
    }
}
