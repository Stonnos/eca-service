package com.ecaservice.auto.test.service.runner;

import com.ecaservice.auto.test.config.mail.MailProperties;
import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.BaseTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.EmailTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentResultsTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.TestFeature;
import com.ecaservice.auto.test.entity.autotest.TestFeatureEntity;
import com.ecaservice.auto.test.entity.autotest.TestFeatureVisitor;
import com.ecaservice.auto.test.model.EmailType;
import com.ecaservice.auto.test.model.ExperimentTestDataModel;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.AutoTestWorkerService;
import com.ecaservice.auto.test.service.ExperimentTestDataProvider;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.service.InstancesLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Experiment auto tests runner.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentAutoTestRunner extends AbstractAutoTestRunner<ExperimentRequestEntity, ExperimentRequest> {

    private static final List<EmailType> WHITELIST_EMAILS =
            List.of(EmailType.NEW, EmailType.IN_PROGRESS, EmailType.FINISHED);

    private final MailProperties mailProperties;
    private final InstancesLoader instancesLoader;
    private final ExperimentTestDataProvider experimentTestDataProvider;

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Constructor with spring dependencies injection.
     *
     * @param autoTestJobService              - auto test job service
     * @param autoTestWorkerService           - auto test worker service
     * @param baseEvaluationRequestRepository - base evaluation request repository
     * @param mailProperties                  - mail properties
     * @param instancesLoader                 - instances loader
     * @param experimentTestDataProvider      - experiment test data provider
     */
    public ExperimentAutoTestRunner(AutoTestJobService autoTestJobService,
                                    AutoTestWorkerService autoTestWorkerService,
                                    BaseEvaluationRequestRepository baseEvaluationRequestRepository,
                                    BaseTestStepRepository baseTestStepRepository,
                                    MailProperties mailProperties,
                                    InstancesLoader instancesLoader,
                                    ExperimentTestDataProvider experimentTestDataProvider) {
        super(AutoTestType.EXPERIMENT_REQUEST_PROCESS, autoTestJobService, autoTestWorkerService,
                baseEvaluationRequestRepository, baseTestStepRepository);
        this.mailProperties = mailProperties;
        this.instancesLoader = instancesLoader;
        this.experimentTestDataProvider = experimentTestDataProvider;
    }

    @Override
    protected ExperimentRequestEntity createSpecificRequestEntity(ExperimentRequest experimentRequest) {
        ExperimentRequestEntity experimentRequestEntity = new ExperimentRequestEntity();
        experimentRequestEntity.setExperimentType(experimentRequest.getExperimentType());
        experimentRequestEntity.setEvaluationMethod(experimentRequest.getEvaluationMethod());
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
    protected List<BaseTestStepEntity> createTestSteps(ExperimentRequestEntity requestEntity,
                                                       List<TestFeatureEntity> features) {
        List<BaseTestStepEntity> steps = newArrayList();
        features.forEach(testFeatureEntity -> {
            var nextSteps = internalCreateTestSteps(requestEntity, testFeatureEntity.getTestFeature());
            steps.addAll(nextSteps);
        });
        return steps;
    }

    private List<BaseTestStepEntity> internalCreateTestSteps(ExperimentRequestEntity requestEntity,
                                                             TestFeature feature) {
        return feature.visit(new TestFeatureVisitor<>() {
            @Override
            public List<BaseTestStepEntity> visitExperimentEmailsFeature() {
                return Stream.of(EmailType.values())
                        .filter(WHITELIST_EMAILS::contains)
                        .map(emailType -> {
                            var emailTestStepEntity = new EmailTestStepEntity();
                            emailTestStepEntity.setEmailType(emailType);
                            emailTestStepEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
                            emailTestStepEntity.setEvaluationRequestEntity(requestEntity);
                            emailTestStepEntity.setCreated(LocalDateTime.now());
                            emailTestStepEntity.setStarted(LocalDateTime.now());
                            return emailTestStepEntity;
                        })
                        .collect(Collectors.toList());
            }

            @Override
            public List<BaseTestStepEntity> visitEvaluationResults() {
                var experimentResultsTestStepEntity = new ExperimentResultsTestStepEntity();
                experimentResultsTestStepEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
                experimentResultsTestStepEntity.setEvaluationRequestEntity(requestEntity);
                experimentResultsTestStepEntity.setCreated(LocalDateTime.now());
                experimentResultsTestStepEntity.setStarted(LocalDateTime.now());
                return Collections.singletonList(experimentResultsTestStepEntity);
            }
        });
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
