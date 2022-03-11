package com.ecaservice.auto.test.service.runner;

import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Abstract auto tests runner class.
 *
 * @param <E> - request entity generic type
 * @param <R> - request generic type
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAutoTestRunner<E extends BaseEvaluationRequestEntity, R> {

    @Getter
    private final AutoTestType autoTestType;
    private final AutoTestJobService autoTestJobService;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;

    /**
     * Creates request entity.
     *
     * @param request - request object
     * @return request entity
     */
    protected abstract E createRequestEntity(R request);

    /**
     * Prepares test requests data.
     *
     * @return test requests list
     */
    protected abstract List<R> prepareAndBuildRequests();

    /**
     * Sends request.
     *
     * @param requestEntity - request entity
     * @param request       - request object
     */
    protected abstract void sendRequest(E requestEntity, R request);

    /**
     * Checks that auto test can be run.
     *
     * @param autoTestsJobEntity - auto tests entity
     * @return {@code true} if auto tests can be run, itherwise {@code false}
     */
    public boolean canRun(AutoTestsJobEntity autoTestsJobEntity) {
        return autoTestType.equals(autoTestsJobEntity.getAutoTestType());
    }

    /**
     * Runs auto tests.
     *
     * @param autoTestsJobEntity - auto tests entity
     */
    public void run(AutoTestsJobEntity autoTestsJobEntity) {
        try {
            List<R> requests = prepareAndBuildRequests();
            requests.forEach(request -> {
                E requestEntity = createRequestEntity(request);
                populateAndSaveRequestEntity(requestEntity, autoTestsJobEntity);
                sendRequest(requestEntity, request);
            });
        } catch (Exception ex) {
            log.error("There was an error while sending requests for job [{}]: {}", autoTestsJobEntity.getJobUuid(),
                    ex.getMessage());
            autoTestJobService.finishWithError(autoTestsJobEntity, ex.getMessage());
        }
    }

    private void populateAndSaveRequestEntity(E experimentRequestEntity,
                                              AutoTestsJobEntity autoTestsJobEntity) {
        experimentRequestEntity.setCorrelationId(UUID.randomUUID().toString());
        experimentRequestEntity.setStageType(RequestStageType.READY);
        experimentRequestEntity.setTestResult(TestResult.UNKNOWN);
        experimentRequestEntity.setExecutionStatus(ExecutionStatus.NEW);
        experimentRequestEntity.setJob(autoTestsJobEntity);
        experimentRequestEntity.setCreated(LocalDateTime.now());
        baseEvaluationRequestRepository.save(experimentRequestEntity);
    }
}
