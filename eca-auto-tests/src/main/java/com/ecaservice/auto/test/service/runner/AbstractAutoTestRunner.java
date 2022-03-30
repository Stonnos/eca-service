package com.ecaservice.auto.test.service.runner;

import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.BaseTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.entity.autotest.TestFeatureEntity;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.AutoTestWorkerService;
import com.ecaservice.base.model.EcaRequest;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
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
    private final BaseTestStepRepository baseTestStepRepository;

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
     * Creates additional test steps entities.
     *
     * @param requestEntity - request entity
     * @param features      - features list
     * @return test steps list
     */
    protected List<BaseTestStepEntity> createTestSteps(E requestEntity, List<TestFeatureEntity> features) {
        return Collections.emptyList();
    }

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
        log.info("Starting to sent auto test [{}] requests", autoTestsJobEntity.getJobUuid());
        try {
            List<R> requests = prepareAndBuildRequests();
            log.info("[{}] test data has been prepared for auto test [{}]", requests.size(),
                    autoTestsJobEntity.getJobUuid());
            requests.forEach(request -> {
                E requestEntity = createSpecificRequestEntity(request);
                populateAndSaveRequestEntityCommonData(requestEntity, request, autoTestsJobEntity);
                populateAndSaveAdditionalTestSteps(autoTestsJobEntity, requestEntity);
                autoTestWorkerService.sendRequest(requestEntity.getId(), request);
            });
            log.info("All auto test [{}] requests has been sent", autoTestsJobEntity.getJobUuid());
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

    private void populateAndSaveAdditionalTestSteps(AutoTestsJobEntity autoTestsJobEntity,
                                                    E requestEntity) {
        if (CollectionUtils.isEmpty(autoTestsJobEntity.getFeatures())) {
            log.info(
                    "No one feature has been specified for auto tests job [{}], request correlation id [{}]. Skipped...",
                    autoTestsJobEntity.getJobUuid(), requestEntity.getCorrelationId());
        } else {
            var testSteps = createTestSteps(requestEntity, autoTestsJobEntity.getFeatures());
            baseTestStepRepository.saveAll(testSteps);
            log.info("[{}] additional test steps has been saved for request correlation id [{}]", testSteps.size(),
                    requestEntity.getCorrelationId());
        }
    }
}
