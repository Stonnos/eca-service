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
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.service.DataLoaderService;
import com.ecaservice.test.common.service.InstancesResourceLoader;
import eca.core.evaluation.EvaluationMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.auto.test.util.Utils.toJson;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Classifier auto tests runner.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ClassifierAutoTestRunner
        extends AbstractAutoTestRunner<EvaluationRequestEntity, ClassifierTestDataModel, EvaluationRequest> {

    private final ClassifierTestDataProvider classifierTestDataProvider;

    /**
     * Constructor with parameters.
     *
     * @param autoTestJobService              - auto test job service
     * @param autoTestWorkerService           - auto test worker service
     * @param baseEvaluationRequestRepository - base evaluation request repository
     * @param instancesResourceLoader         - instances resource loader
     * @param dataLoaderService               - data loader service
     * @param classifierTestDataProvider      - classifier test data provider
     */
    public ClassifierAutoTestRunner(AutoTestJobService autoTestJobService,
                                    AutoTestWorkerService autoTestWorkerService,
                                    BaseEvaluationRequestRepository baseEvaluationRequestRepository,
                                    BaseTestStepRepository baseTestStepRepository,
                                    InstancesResourceLoader instancesResourceLoader,
                                    DataLoaderService dataLoaderService,
                                    ClassifierTestDataProvider classifierTestDataProvider) {
        super(AutoTestType.EVALUATION_REQUEST_PROCESS, autoTestJobService, autoTestWorkerService,
                instancesResourceLoader, dataLoaderService, baseEvaluationRequestRepository, baseTestStepRepository);
        this.classifierTestDataProvider = classifierTestDataProvider;
    }

    @Override
    protected EvaluationRequestEntity createSpecificRequestEntity(ClassifierTestDataModel testDataModel,
                                                                  EvaluationRequest evaluationRequest) {
        EvaluationRequestEntity evaluationRequestEntity = new EvaluationRequestEntity();
        evaluationRequestEntity.setClassifierName(evaluationRequest.getClassifierOptions().getClass().getSimpleName());
        evaluationRequestEntity.setClassifierOptions(toJson(evaluationRequest.getClassifierOptions()));
        evaluationRequestEntity.setEvaluationMethod(testDataModel.getEvaluationMethod());
        evaluationRequestEntity.setNumFolds(testDataModel.getNumFolds());
        evaluationRequestEntity.setNumTests(testDataModel.getNumTests());
        evaluationRequestEntity.setSeed(testDataModel.getSeed());
        return evaluationRequestEntity;
    }

    @Override
    protected EvaluationRequest createEcaRequest(ClassifierTestDataModel classifierTestDataModel, String dataUuid) {
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setDataUuid(dataUuid);
        evaluationRequest.setClassifierOptions(classifierTestDataModel.getClassifierOptions());
        evaluationRequest.setEvaluationMethod(classifierTestDataModel.getEvaluationMethod());
        if (EvaluationMethod.CROSS_VALIDATION.equals(classifierTestDataModel.getEvaluationMethod())) {
            evaluationRequest.setNumFolds(classifierTestDataModel.getNumFolds());
            evaluationRequest.setNumTests(classifierTestDataModel.getNumTests());
            evaluationRequest.setSeed(classifierTestDataModel.getSeed());
        }
        return evaluationRequest;
    }

    @Override
    protected List<ClassifierTestDataModel> getTestDataModels() {
        return classifierTestDataProvider.getTestDataModels();
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
}
