package com.ecaservice.auto.test.service.executor;

import com.ecaservice.auto.test.config.mail.MailProperties;
import com.ecaservice.auto.test.entity.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.ExperimentRequestStageType;
import com.ecaservice.auto.test.model.ExperimentTestDataModel;
import com.ecaservice.auto.test.repository.AutoTestsJobRepository;
import com.ecaservice.auto.test.repository.ExperimentRequestRepository;
import com.ecaservice.auto.test.service.AutoTestWorkerService;
import com.ecaservice.auto.test.service.ExperimentTestDataProvider;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import com.ecaservice.test.common.service.InstancesLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Auto tests executor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTestExecutor {

    private final MailProperties mailProperties;
    private final InstancesLoader instancesLoader;
    private final ExperimentTestDataProvider experimentTestDataProvider;
    private final AutoTestWorkerService autoTestWorkerService;
    private final AutoTestsJobRepository autoTestsJobRepository;
    private final ExperimentRequestRepository experimentRequestRepository;

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Runs auto tests.
     *
     * @param autoTestsJobEntity - load test entity
     */
    public void runTests(AutoTestsJobEntity autoTestsJobEntity) {
        log.info("Runs new tests job with uuid [{}]", autoTestsJobEntity.getJobUuid());
        autoTestsJobEntity.setStarted(LocalDateTime.now());
        autoTestsJobEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
        autoTestsJobRepository.save(autoTestsJobEntity);
        sendRequests(autoTestsJobEntity);
        log.info("Auto tests job [{}] has been started", autoTestsJobEntity.getJobUuid());
    }

    private void sendRequests(AutoTestsJobEntity autoTestsJobEntity) {
        try {
            List<ExperimentRequest> experimentRequests = prepareAndBuildExperimentRequests();
            experimentRequests.forEach(experimentRequest -> {
                ExperimentRequestEntity experimentRequestEntity =
                        createAndSaveExperimentRequestEntity(experimentRequest, autoTestsJobEntity);
                autoTestWorkerService.sendRequest(experimentRequestEntity.getId(), experimentRequest);
            });
        } catch (Exception ex) {
            log.error("There was an error while sending requests for job [{}]: {}", autoTestsJobEntity.getJobUuid(),
                    ex.getMessage());
            finishWithError(autoTestsJobEntity, ex);
        }
    }

    private List<ExperimentRequest> prepareAndBuildExperimentRequests() {
        return experimentTestDataProvider.getTestDataModels().stream()
                .map(this::createExperimentRequest)
                .collect(Collectors.toList());
    }

    private ExperimentRequest createExperimentRequest(ExperimentTestDataModel experimentTestDataModel) {
        Resource instancesResource = resolver.getResource(experimentTestDataModel.getTrainDataPath());
        Instances instances = instancesLoader.loadInstances(instancesResource);
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setData(instances);
        experimentRequest.setEmail(mailProperties.getUserName());
        experimentRequest.setFirstName(experimentTestDataModel.getFirstName());
        experimentRequest.setEvaluationMethod(experimentTestDataModel.getEvaluationMethod());
        experimentRequest.setExperimentType(experimentTestDataModel.getExperimentType());
        return experimentRequest;
    }

    private ExperimentRequestEntity createAndSaveExperimentRequestEntity(ExperimentRequest experimentRequest,
                                                                         AutoTestsJobEntity autoTestsJobEntity) {
        ExperimentRequestEntity experimentRequestEntity = new ExperimentRequestEntity();
        experimentRequestEntity.setCorrelationId(UUID.randomUUID().toString());
        experimentRequestEntity.setExperimentType(experimentRequest.getExperimentType());
        experimentRequestEntity.setEvaluationMethod(experimentRequest.getEvaluationMethod());
        experimentRequestEntity.setStageType(ExperimentRequestStageType.READY);
        experimentRequestEntity.setTestResult(TestResult.UNKNOWN);
        experimentRequestEntity.setExecutionStatus(ExecutionStatus.NEW);
        experimentRequestEntity.setJob(autoTestsJobEntity);
        Instances instances = experimentRequest.getData();
        experimentRequestEntity.setRelationName(instances.relationName());
        experimentRequestEntity.setNumAttributes(instances.numAttributes());
        experimentRequestEntity.setNumInstances(instances.numInstances());
        experimentRequestEntity.setCreated(LocalDateTime.now());
        return experimentRequestRepository.save(experimentRequestEntity);
    }

    private void finishWithError(AutoTestsJobEntity loadTestEntity, Exception ex) {
        loadTestEntity.setDetails(ex.getMessage());
        loadTestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        loadTestEntity.setFinished(LocalDateTime.now());
        autoTestsJobRepository.save(loadTestEntity);
    }
}
