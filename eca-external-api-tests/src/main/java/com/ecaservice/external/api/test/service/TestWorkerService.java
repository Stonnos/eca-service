package com.ecaservice.external.api.test.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.bpm.service.ProcessManager;
import com.ecaservice.external.api.test.config.ProcessConfig;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.ExecutionStatus;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_RESULTS_MATCHER;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_TYPE;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Test worker service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestWorkerService {

    private final ProcessConfig processConfig;
    private final ObjectMapper objectMapper;
    private final ProcessManager processManager;
    private final AutoTestService autoTestService;
    private final AutoTestRepository autoTestRepository;

    /**
     * Executes auto test.
     *
     * @param testId         - test id
     * @param testDataModel  - test data model
     * @param countDownLatch - count down latch
     */
    public void execute(long testId, TestDataModel testDataModel, CountDownLatch countDownLatch) {
        AutoTestEntity autoTestEntity = autoTestRepository.findById(testId).orElseThrow(
                () -> new EntityNotFoundException(AutoTestEntity.class, testId));
        try {
            autoTestEntity.setRequest(objectMapper.writeValueAsString(testDataModel.getRequest()));
            autoTestEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
            autoTestEntity.setStarted(LocalDateTime.now());
            autoTestRepository.save(autoTestEntity);
            executeNextTest(autoTestEntity, testDataModel);
        } catch (Exception ex) {
            log.error("Unknown error while auto test [{}] execution: {}", autoTestEntity.getId(), ex.getMessage(), ex);
            autoTestService.finishWithError(autoTestEntity.getId(), ex.getMessage());
        } finally {
            countDownLatch.countDown();
        }
    }

    private void executeNextTest(AutoTestEntity autoTestEntity, TestDataModel testDataModel) {
        TestResultsMatcher matcher = new TestResultsMatcher();
        Map<String, Object> variables = newHashMap();
        variables.put(AUTO_TEST_ID, autoTestEntity.getId());
        variables.put(TEST_TYPE, testDataModel.getTestType().name());
        variables.put(TEST_DATA_MODEL, testDataModel);
        variables.put(TEST_RESULTS_MATCHER, matcher);
        processManager.startProcess(processConfig.getProcessId(), UUID.randomUUID().toString(), variables);
    }
}
