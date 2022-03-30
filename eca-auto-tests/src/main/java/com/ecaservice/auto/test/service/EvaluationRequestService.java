package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.dto.BaseEvaluationRequestDto;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.projections.TestResultProjection;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.service.step.TestStepService;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.auto.test.util.Utils.calculateFinalTestResult;
import static com.ecaservice.auto.test.util.Utils.sum;

/**
 * Auto test service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationRequestService {

    private static final List<ExecutionStatus> IN_PROGRESS_STATUSES = List.of(
            ExecutionStatus.NEW,
            ExecutionStatus.IN_PROGRESS
    );

    private final AutoTestsProperties autoTestsProperties;
    private final TestStepService testStepService;
    private final EvaluationRequestAdapter evaluationRequestAdapter;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;
    private final BaseTestStepRepository testStepRepository;

    /**
     * Gets evaluation request details by request id.
     *
     * @param requestId - request id from eca - server
     * @return evaluation request details dto
     */
    public BaseEvaluationRequestDto getEvaluationRequestDetails(String requestId) {
        log.info("Gets evaluation request details for request id [{}]", requestId);
        var evaluationRequestEntity = baseEvaluationRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new EntityNotFoundException(BaseEvaluationRequestEntity.class, requestId));
        var evaluationRequestDto = evaluationRequestAdapter.proceed(evaluationRequestEntity);
        return evaluationRequestDto;
    }

    /**
     * Calculates final test result and finishes test execution.
     *
     * @param requestEntity - request entity
     */
    public void complete(BaseEvaluationRequestEntity requestEntity) {
        var testSteps = testStepRepository.getTestResults(requestEntity);
        TestResult testResult = calculateFinalTestResult(testSteps);
        int totalMatched = sum(testSteps, TestResultProjection::getTotalMatched);
        int totalNotMatched = sum(testSteps, TestResultProjection::getTotalNotMatched);
        int totalNotFound = sum(testSteps, TestResultProjection::getTotalNotFound);
        requestEntity.setTotalMatched(totalMatched);
        requestEntity.setTotalNotMatched(totalNotMatched);
        requestEntity.setTotalNotFound(totalNotFound);
        requestEntity.setTestResult(testResult);
        requestEntity.setExecutionStatus(ExecutionStatus.FINISHED);
        requestEntity.setFinished(LocalDateTime.now());
        baseEvaluationRequestRepository.save(requestEntity);
        log.info("Evaluation request [{}] test has been finished with result: {}", requestEntity.getRequestId(),
                requestEntity.getTestResult());
    }

    /**
     * Finish request entity test with error.
     *
     * @param requestEntity - request entity
     * @param errorMessage  - error message
     */
    @Transactional
    public void finishWithError(BaseEvaluationRequestEntity requestEntity, String errorMessage) {
        requestEntity.setStageType(RequestStageType.ERROR);
        requestEntity.setTestResult(TestResult.ERROR);
        requestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        requestEntity.setDetails(errorMessage);
        finishTestStepsWithError(requestEntity, errorMessage);
        requestEntity.setFinished(LocalDateTime.now());
        baseEvaluationRequestRepository.save(requestEntity);
        log.debug("Evaluation auto test [{}] has been finished with error", requestEntity.getId());
    }

    /**
     * Exceeds request.
     *
     * @param requestEntity - request entity
     */
    public void exceed(BaseEvaluationRequestEntity requestEntity) {
        requestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        requestEntity.setStageType(RequestStageType.EXCEEDED);
        requestEntity.setTestResult(TestResult.ERROR);
        String details = String.format("Request timeout exceeded after [%d] seconds!",
                autoTestsProperties.getRequestTimeoutInSeconds());
        requestEntity.setDetails(details);
        requestEntity.setFinished(LocalDateTime.now());
        baseEvaluationRequestRepository.save(requestEntity);
        log.info("Exceeded request with correlation id [{}]", requestEntity.getCorrelationId());
    }

    private void finishTestStepsWithError(BaseEvaluationRequestEntity requestEntity, String errorMessage) {
        var testSteps = testStepRepository.findAllByEvaluationRequestEntityAndExecutionStatusIn(requestEntity,
                IN_PROGRESS_STATUSES);
        if (!CollectionUtils.isEmpty(testSteps)) {
            log.info("Got additional test steps [{}] for request id [{}] to finish with error",
                    requestEntity.getRequestId(), testSteps.size());
            testStepService.finishWithError(testSteps, errorMessage);
        }
    }
}
