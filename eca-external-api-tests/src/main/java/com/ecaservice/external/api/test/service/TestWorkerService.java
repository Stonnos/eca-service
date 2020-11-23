package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.ExecutionStatus;
import com.ecaservice.external.api.test.entity.TestResult;
import com.ecaservice.external.api.test.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;

/**
 * Test worker service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestWorkerService {

    private final AutoTestRepository autoTestRepository;

    public void execute(long testId, TestDataModel testDataModel, CountDownLatch countDownLatch) {
        AutoTestEntity autoTestEntity = autoTestRepository.findById(testId).orElseThrow(
                () -> new EntityNotFoundException(AutoTestEntity.class, testId));
        try {
            autoTestEntity.setStarted(LocalDateTime.now());
            autoTestEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
            autoTestRepository.save(autoTestEntity);
            sendRequest(autoTestEntity, testDataModel);
        } catch (Exception ex) {
            log.error("Unknown error while auto test [{}] execution: {}", autoTestEntity.getId(), ex.getMessage(), ex);
            handleError(autoTestEntity, ex);
        } finally {
            autoTestEntity.setFinished(LocalDateTime.now());
            autoTestRepository.save(autoTestEntity);
            countDownLatch.countDown();
        }
    }

    private void sendRequest(AutoTestEntity autoTestEntity, TestDataModel testDataModel) {

    }

    private void handleError(AutoTestEntity autoTestEntity, Exception ex) {
        autoTestEntity.setTestResult(TestResult.ERROR);
        autoTestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        autoTestEntity.setDetails(ex.getMessage());
    }
}
