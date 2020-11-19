package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.ExecutionStatus;
import com.ecaservice.external.api.test.entity.TestResult;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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

    public void execute(long testId, EvaluationRequestDto evaluationRequestDto, CountDownLatch countDownLatch) {
        AutoTestEntity autoTestEntity = autoTestRepository.findById(testId).orElseThrow(
                IllegalStateException::new);
        try {
            autoTestEntity.setStarted(LocalDateTime.now());
            autoTestEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
            autoTestRepository.save(autoTestEntity);
            sendRequest(autoTestEntity, evaluationRequestDto);
        } catch (Exception ex) {
            log.error("Unknown error while auto test [{}] execution: {}", autoTestEntity.getId(), ex.getMessage(), ex);
            handleError(autoTestEntity, ex);
        } finally {
            autoTestEntity.setFinished(LocalDateTime.now());
            autoTestRepository.save(autoTestEntity);
            countDownLatch.countDown();
        }
    }

    private void sendRequest(AutoTestEntity autoTestEntity , EvaluationRequestDto evaluationRequestDto) {

    }

    private void handleError(AutoTestEntity autoTestEntity, Exception ex) {
        autoTestEntity.setTestResult(TestResult.ERROR);
        autoTestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        autoTestEntity.setDetails(ex.getMessage());
    }
}
