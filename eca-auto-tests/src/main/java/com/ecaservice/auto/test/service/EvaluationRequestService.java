package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.service.step.TestStepService;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;

/**
 * Auto test service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationRequestService {

    private final AutoTestsProperties autoTestsProperties;
    private final TestStepService testStepService;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;
    private final BaseTestStepRepository baseTestStepRepository;

    /**
     * Finish request entity test with error.
     *
     * @param requestEntity - request entity
     * @param errorMessage  - error message
     */
    public void finishWithError(BaseEvaluationRequestEntity requestEntity, String errorMessage) {
        requestEntity.setStageType(RequestStageType.ERROR);
        requestEntity.setTestResult(TestResult.ERROR);
        requestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        requestEntity.setDetails(errorMessage);
        requestEntity.setFinished(LocalDateTime.now());
        baseEvaluationRequestRepository.save(requestEntity);
        log.debug("Evaluation auto test [{}] has been finished with error", requestEntity.getId());
    }

    /**
     * Exceeds request.
     *
     * @param requestEntity - request entity
     */
    @Transactional
    public void exceed(BaseEvaluationRequestEntity requestEntity) {
        requestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        requestEntity.setStageType(RequestStageType.EXCEEDED);
        requestEntity.setTestResult(TestResult.ERROR);
        String details = String.format("Request timeout exceeded after [%d] seconds!",
                autoTestsProperties.getRequestTimeoutInSeconds());
        exceedAllTestSteps(requestEntity, details);
        requestEntity.setDetails(details);
        requestEntity.setFinished(LocalDateTime.now());
        baseEvaluationRequestRepository.save(requestEntity);
        log.info("Exceeded request with correlation id [{}]", requestEntity.getCorrelationId());
    }

    private void exceedAllTestSteps(BaseEvaluationRequestEntity requestEntity, String message) {
        var testSteps = baseTestStepRepository.findAllByEvaluationRequestEntity(requestEntity);
        if (CollectionUtils.isEmpty(testSteps)) {
            log.info("Got empty additional test steps to exceed for request id [{}]. Skipped...",
                    requestEntity.getRequestId());
        } else {
            testSteps.forEach(testStepEntity -> testStepService.finishWithError(testStepEntity, message));
        }
    }
}
