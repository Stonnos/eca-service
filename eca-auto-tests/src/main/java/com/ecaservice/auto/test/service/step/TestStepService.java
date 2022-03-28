package com.ecaservice.auto.test.service.step;

import com.ecaservice.auto.test.entity.autotest.BaseTestStepEntity;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Test step service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestStepService {

    private final BaseTestStepRepository baseTestStepRepository;

    /**
     * Finishes test step with error.
     *
     * @param testStepEntity - test step entity
     * @param errorMessage   - error message
     */
    public void finishWithError(BaseTestStepEntity testStepEntity, String errorMessage) {
        testStepEntity.setDetails(errorMessage);
        testStepEntity.setTestResult(TestResult.ERROR);
        testStepEntity.setExecutionStatus(ExecutionStatus.ERROR);
        testStepEntity.setFinished(LocalDateTime.now());
        baseTestStepRepository.save(testStepEntity);
        log.info("Test step [{}] has been finished with error", testStepEntity.getId());
    }

    /**
     * Completes test step.
     *
     * @param testStepEntity - test step entity
     */
    public void complete(BaseTestStepEntity testStepEntity) {
        testStepEntity.setExecutionStatus(ExecutionStatus.FINISHED);
        testStepEntity.setFinished(LocalDateTime.now());
        baseTestStepRepository.save(testStepEntity);
        log.info("Test step [{}] has been finished", testStepEntity.getId());
    }
}
