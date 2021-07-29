package com.ecaservice.auto.test.service.executor;

import com.ecaservice.auto.test.config.AutoTestsProperties;
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
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Auto tests executor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTestExecutor {

    private final AutoTestsProperties autoTestsProperties;
    private final MailProperties mailProperties;
    private final InstancesLoader instancesLoader;
    private final ExperimentTestDataProvider experimentTestDataProvider;
    private final AutoTestWorkerService autoTestWorkerService;
    private final AutoTestsJobRepository autoTestsJobRepository;
    private final ExperimentRequestRepository experimentRequestRepository;

    /**
     * Runs auto tests.
     *
     * @param autoTestsJobEntity - load test entity
     */
    public void runTests(AutoTestsJobEntity autoTestsJobEntity) {
        log.info("Runs new test with uuid [{}]", autoTestsJobEntity.getJobUuid());
        autoTestsJobEntity.setStarted(LocalDateTime.now());
        autoTestsJobEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
        autoTestsJobRepository.save(autoTestsJobEntity);
        sendRequests(autoTestsJobEntity);
        log.info("Test [{}] has been started", autoTestsJobEntity.getJobUuid());
    }

    private void sendRequests(AutoTestsJobEntity autoTestsJobEntity) {
        experimentTestDataProvider.getTestDataModels().forEach(experimentTestDataModel -> {
            ExperimentRequest experimentRequest = createExperimentRequest(experimentTestDataModel);
            ExperimentRequestEntity experimentRequestEntity =
                    createAndSaveExperimentRequestEntity(experimentRequest, autoTestsJobEntity);
            autoTestWorkerService.sendRequest(experimentRequestEntity.getId(), experimentRequest);
        });
    }

    private ExperimentRequest createExperimentRequest(ExperimentTestDataModel experimentTestDataModel) {
        Instances instances = instancesLoader.loadInstances(experimentTestDataModel.getTrainDataPath());
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setData(instances);
        experimentRequest.setEmail(mailProperties.getUserName());
        experimentRequest.setFirstName(autoTestsProperties.getFirstName());
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
}
