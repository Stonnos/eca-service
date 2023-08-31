package com.ecaservice.auto.test.service.runner;

import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.BaseTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.entity.autotest.TestFeatureEntity;
import com.ecaservice.auto.test.model.AbstractEvaluationTestDataModel;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.AutoTestWorkerService;
import com.ecaservice.base.model.EcaRequest;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import com.ecaservice.test.common.service.DataLoaderService;
import com.ecaservice.test.common.service.InstancesResourceLoader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.CollectionUtils;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Abstract auto tests runner class.
 *
 * @param <E> - request entity generic type
 * @param <R> - test data model generic type
 * @param <T> - eca request generic type
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAutoTestRunner<E extends BaseEvaluationRequestEntity,
        R extends AbstractEvaluationTestDataModel, T extends EcaRequest> {

    @Getter
    private final AutoTestType autoTestType;
    private final AutoTestJobService autoTestJobService;
    private final AutoTestWorkerService autoTestWorkerService;
    private final InstancesResourceLoader instancesResourceLoader;
    private final DataLoaderService dataLoaderService;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;
    private final BaseTestStepRepository baseTestStepRepository;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Creates request entity with specific data.
     *
     * @param testDataModel - test data model
     * @param ecaRequest    - eca request
     * @return request entity
     */
    protected abstract E createSpecificRequestEntity(R testDataModel, T ecaRequest);

    /**
     * Creates eca request.
     *
     * @param testDataModel - test data model
     * @param dataUuid      - training data uuid
     * @return request entity
     */
    protected abstract T createEcaRequest(R testDataModel, String dataUuid);

    /**
     * Gets test data models.
     *
     * @return test data models
     */
    protected abstract List<R> getTestDataModels();

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
            List<R> testDataModels = getTestDataModels();
            log.info("[{}] test data models has been fetched for auto test [{}]", testDataModels.size(),
                    autoTestsJobEntity.getJobUuid());
            Map<String, String> dataUuidMap = uploadTrainDataSets(testDataModels);
            testDataModels.forEach(testDataModel -> {
                String dataUuid = dataUuidMap.get(testDataModel.getTrainDataPath());
                T ecaRequest = createEcaRequest(testDataModel, dataUuid);
                E requestEntity = createSpecificRequestEntity(testDataModel, ecaRequest);
                populateAndSaveRequestEntityCommonData(requestEntity, testDataModel, autoTestsJobEntity);
                populateAndSaveAdditionalTestSteps(autoTestsJobEntity, requestEntity);
                autoTestWorkerService.sendRequest(requestEntity.getId(), ecaRequest);
            });
            log.info("All auto test [{}] requests has been sent", autoTestsJobEntity.getJobUuid());
        } catch (Exception ex) {
            log.error("There was an error while sending requests for job [{}]: {}", autoTestsJobEntity.getJobUuid(),
                    ex.getMessage());
            autoTestJobService.finishWithError(autoTestsJobEntity, ex.getMessage());
        }
    }

    private Map<String, String> uploadTrainDataSets(List<R> testDataModels) {
        Set<String> dataPaths = testDataModels.stream()
                .map(AbstractEvaluationTestDataModel::getTrainDataPath)
                .collect(Collectors.toSet());
        log.info("Starting to upload [{}] instances files to data storage", dataPaths.size());
        Map<String, String> dataUuidMap = newHashMap();
        dataPaths.forEach(dataPath -> {
            var resource = resolver.getResource(dataPath);
            String dataUuid = dataLoaderService.uploadInstances(resource);
            dataUuidMap.put(dataPath, dataUuid);
        });
        log.info("[{}] instances files has been uploaded to data storage", dataUuidMap.size());
        return dataUuidMap;
    }

    private void populateAndSaveRequestEntityCommonData(E requestEntity,
                                                        R testDataModel,
                                                        AutoTestsJobEntity autoTestsJobEntity) {
        var resource = resolver.getResource(testDataModel.getTrainDataPath());
        Instances data = instancesResourceLoader.loadInstances(resource);
        requestEntity.setNumAttributes(data.numAttributes());
        requestEntity.setNumInstances(data.numInstances());
        requestEntity.setRelationName(data.relationName());
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
