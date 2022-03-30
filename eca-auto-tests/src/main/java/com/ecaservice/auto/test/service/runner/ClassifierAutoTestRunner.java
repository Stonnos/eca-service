package com.ecaservice.auto.test.service.runner;

import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.BaseTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationResultsTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.TestFeature;
import com.ecaservice.auto.test.entity.autotest.TestFeatureEntity;
import com.ecaservice.auto.test.entity.autotest.TestFeatureVisitor;
import com.ecaservice.auto.test.model.ClassifierTestDataModel;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.AutoTestWorkerService;
import com.ecaservice.auto.test.service.ClassifierTestDataProvider;
import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.service.InstancesLoader;
import eca.core.evaluation.EvaluationMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.auto.test.util.Utils.toJson;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Classifier auto tests runner.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ClassifierAutoTestRunner extends AbstractAutoTestRunner<EvaluationRequestEntity, EvaluationRequest> {

    private final InstancesLoader instancesLoader;
    private final ClassifierTestDataProvider classifierTestDataProvider;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Constructor with spring dependencies injection.
     *
     * @param autoTestJobService              - auto test job service
     * @param autoTestWorkerService           - auto test worker service
     * @param baseEvaluationRequestRepository - base evaluation request repository
     * @param instancesLoader                 - instances loader
     * @param classifierTestDataProvider      - classifier test data provider
     * @param classifierOptionsAdapter        - classifier options adapter
     */
    public ClassifierAutoTestRunner(AutoTestJobService autoTestJobService,
                                    AutoTestWorkerService autoTestWorkerService,
                                    BaseEvaluationRequestRepository baseEvaluationRequestRepository,
                                    BaseTestStepRepository baseTestStepRepository,
                                    InstancesLoader instancesLoader,
                                    ClassifierTestDataProvider classifierTestDataProvider,
                                    ClassifierOptionsAdapter classifierOptionsAdapter) {
        super(AutoTestType.EVALUATION_REQUEST_PROCESS, autoTestJobService, autoTestWorkerService,
                baseEvaluationRequestRepository, baseTestStepRepository);
        this.instancesLoader = instancesLoader;
        this.classifierTestDataProvider = classifierTestDataProvider;
        this.classifierOptionsAdapter = classifierOptionsAdapter;
    }

    @Override
    protected EvaluationRequestEntity createSpecificRequestEntity(EvaluationRequest evaluationRequest) {
        EvaluationRequestEntity evaluationRequestEntity = new EvaluationRequestEntity();
        evaluationRequestEntity.setClassifierName(evaluationRequest.getClassifier().getClass().getSimpleName());
        var classifierOptions = classifierOptionsAdapter.convert(evaluationRequest.getClassifier());
        evaluationRequestEntity.setClassifierOptions(toJson(classifierOptions));
        evaluationRequestEntity.setEvaluationMethod(evaluationRequest.getEvaluationMethod());
        evaluationRequestEntity.setNumFolds(evaluationRequest.getNumFolds());
        evaluationRequestEntity.setNumTests(evaluationRequest.getNumTests());
        evaluationRequestEntity.setSeed(evaluationRequest.getSeed());
        return evaluationRequestEntity;
    }

    @Override
    protected List<EvaluationRequest> prepareAndBuildRequests() {
        return classifierTestDataProvider.getTestDataModels()
                .stream()
                .map(this::createEvaluationRequest)
                .collect(Collectors.toList());
    }

    @Override
    protected List<BaseTestStepEntity> createTestSteps(EvaluationRequestEntity requestEntity,
                                                       List<TestFeatureEntity> features) {
        List<BaseTestStepEntity> steps = newArrayList();
        features.forEach(testFeatureEntity -> {
            var nextSteps = internalCreateTestSteps(requestEntity, testFeatureEntity.getTestFeature());
            steps.addAll(nextSteps);
        });
        return steps;
    }

    private List<BaseTestStepEntity> internalCreateTestSteps(EvaluationRequestEntity requestEntity,
                                                             TestFeature feature) {
        return feature.visit(new TestFeatureVisitor<>() {
            @Override
            public List<BaseTestStepEntity> visitEvaluationResults() {
                var evaluationResultsTestStepEntity = new EvaluationResultsTestStepEntity();
                evaluationResultsTestStepEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
                evaluationResultsTestStepEntity.setEvaluationRequestEntity(requestEntity);
                evaluationResultsTestStepEntity.setCreated(LocalDateTime.now());
                evaluationResultsTestStepEntity.setStarted(LocalDateTime.now());
                return Collections.singletonList(evaluationResultsTestStepEntity);
            }
        });
    }

    private EvaluationRequest createEvaluationRequest(ClassifierTestDataModel classifierTestDataModel) {
        Resource instancesResource = resolver.getResource(classifierTestDataModel.getTrainDataPath());
        Instances instances = instancesLoader.loadInstances(instancesResource);
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setData(instances);
        AbstractClassifier classifier =
                classifierOptionsAdapter.convert(classifierTestDataModel.getClassifierOptions());
        evaluationRequest.setClassifier(classifier);
        evaluationRequest.setEvaluationMethod(classifierTestDataModel.getEvaluationMethod());
        if (EvaluationMethod.CROSS_VALIDATION.equals(classifierTestDataModel.getEvaluationMethod())) {
            evaluationRequest.setNumFolds(classifierTestDataModel.getNumFolds());
            evaluationRequest.setNumTests(classifierTestDataModel.getNumTests());
            evaluationRequest.setSeed(classifierTestDataModel.getSeed());
        }
        return evaluationRequest;
    }
}
