package com.ecaservice.auto.test.service.runner;

import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.AutoTestWorkerService;
import com.ecaservice.base.model.EcaRequest;
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
public abstract class AbstractAutoTestRunner<E extends BaseEvaluationRequestEntity, R extends EcaRequest> {

    @Getter
    private final AutoTestType autoTestType;
    private final AutoTestJobService autoTestJobService;
    private final AutoTestWorkerService autoTestWorkerService;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;

    /**
     * Creates request entity with specific data.
     *
     * @param request - request object
     * @return request entity
     */
    protected abstract E createSpecificRequestEntity(R request);

    /**
     * Prepares test requests data.
     *
     * @return test requests list
     */
    protected abstract List<R> prepareAndBuildRequests();

    /**
     * Checks that auto test can be run.
     *
     * @param autoTestsJobEntity - auto tests entity
     * @return {@code true} if auto tests can be run, otherwise {@code false}
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
                E requestEntity = createSpecificRequestEntity(request);
                populateAndSaveRequestEntityCommonData(requestEntity, request, autoTestsJobEntity);
                autoTestWorkerService.sendRequest(requestEntity.getId(), request);
            });
        } catch (Exception ex) {
            log.error("There was an error while sending requests for job [{}]: {}", autoTestsJobEntity.getJobUuid(),
                    ex.getMessage());
            autoTestJobService.finishWithError(autoTestsJobEntity, ex.getMessage());
        }
    }

    private void populateAndSaveRequestEntityCommonData(E requestEntity, R request,
                                                        AutoTestsJobEntity autoTestsJobEntity) {
        requestEntity.setNumAttributes(request.getData().numAttributes());
        requestEntity.setNumInstances(request.getData().numInstances());
        requestEntity.setRelationName(request.getData().relationName());
        requestEntity.setCorrelationId(UUID.randomUUID().toString());
        requestEntity.setStageType(RequestStageType.READY);
        requestEntity.setTestResult(TestResult.UNKNOWN);
        requestEntity.setExecutionStatus(ExecutionStatus.NEW);
        requestEntity.setJob(autoTestsJobEntity);
        requestEntity.setCreated(LocalDateTime.now());
        baseEvaluationRequestRepository.save(requestEntity);
    }
}
