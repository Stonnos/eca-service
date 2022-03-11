package com.ecaservice.auto.test.service.runner;

import com.ecaservice.auto.test.config.mail.MailProperties;
import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.model.ExperimentTestDataModel;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.AutoTestWorkerService;
import com.ecaservice.auto.test.service.ExperimentTestDataProvider;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.test.common.service.InstancesLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Experiment auto tests runner.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentAutoTestRunner extends AbstractAutoTestRunner<ExperimentRequestEntity, ExperimentRequest> {

    private final MailProperties mailProperties;
    private final InstancesLoader instancesLoader;
    private final ExperimentTestDataProvider experimentTestDataProvider;
    private final AutoTestWorkerService autoTestWorkerService;

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Constructor with spring dependencies injection.
     *
     * @param autoTestJobService              - auto test job service
     * @param baseEvaluationRequestRepository - base evaluation request repository
     * @param mailProperties                  - mail properties
     * @param instancesLoader                 - instances loader
     * @param experimentTestDataProvider      - experiment test data provider
     * @param autoTestWorkerService           - auto test worker service
     */
    public ExperimentAutoTestRunner(AutoTestJobService autoTestJobService,
                                    BaseEvaluationRequestRepository baseEvaluationRequestRepository,
                                    MailProperties mailProperties,
                                    InstancesLoader instancesLoader,
                                    ExperimentTestDataProvider experimentTestDataProvider,
                                    AutoTestWorkerService autoTestWorkerService) {
        super(AutoTestType.EXPERIMENT_REQUEST_PROCESS, autoTestJobService, baseEvaluationRequestRepository);
        this.mailProperties = mailProperties;
        this.instancesLoader = instancesLoader;
        this.experimentTestDataProvider = experimentTestDataProvider;
        this.autoTestWorkerService = autoTestWorkerService;
    }

    @Override
    protected ExperimentRequestEntity createRequestEntity(ExperimentRequest experimentRequest) {
        ExperimentRequestEntity experimentRequestEntity = new ExperimentRequestEntity();
        experimentRequestEntity.setExperimentType(experimentRequest.getExperimentType());
        experimentRequestEntity.setEvaluationMethod(experimentRequest.getEvaluationMethod());
        Instances instances = experimentRequest.getData();
        experimentRequestEntity.setRelationName(instances.relationName());
        experimentRequestEntity.setNumAttributes(instances.numAttributes());
        experimentRequestEntity.setNumInstances(instances.numInstances());
        return experimentRequestEntity;
    }

    @Override
    protected List<ExperimentRequest> prepareAndBuildRequests() {
        return experimentTestDataProvider.getTestDataModels()
                .stream()
                .map(this::createExperimentRequest)
                .collect(Collectors.toList());
    }

    @Override
    protected void sendRequest(ExperimentRequestEntity requestEntity, ExperimentRequest request) {
        autoTestWorkerService.sendRequest(requestEntity.getId(), request);
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
}
