package com.ecaservice.auto.test.service.runner;

import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.EvaluationRequestEntity;
import com.ecaservice.auto.test.model.ClassifierTestDataModel;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.AutoTestWorkerService;
import com.ecaservice.auto.test.service.ClassifierTestDataProvider;
import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.test.common.service.InstancesLoader;
import eca.core.evaluation.EvaluationMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.util.List;
import java.util.stream.Collectors;

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

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Constructor with spring dependencies injection.
     *
     * @param autoTestJobService              - auto test job service
     * @param autoTestWorkerService           - auto test worker service
     * @param baseEvaluationRequestRepository - base evaluation request repository
     * @param instancesLoader                 - instances loader
     * @param classifierTestDataProvider      - classifier test data provider
     */
    public ClassifierAutoTestRunner(AutoTestJobService autoTestJobService,
                                    AutoTestWorkerService autoTestWorkerService,
                                    BaseEvaluationRequestRepository baseEvaluationRequestRepository,
                                    InstancesLoader instancesLoader,
                                    ClassifierTestDataProvider classifierTestDataProvider) {
        super(AutoTestType.EVALUATION_REQUEST_PROCESS, autoTestJobService, autoTestWorkerService,
                baseEvaluationRequestRepository);
        this.instancesLoader = instancesLoader;
        this.classifierTestDataProvider = classifierTestDataProvider;
    }

    @Override
    protected EvaluationRequestEntity createRequestEntity(EvaluationRequest evaluationRequest) {
        EvaluationRequestEntity evaluationRequestEntity = new EvaluationRequestEntity();
        evaluationRequestEntity.setEvaluationMethod(evaluationRequest.getEvaluationMethod());
        Instances instances = evaluationRequest.getData();
        evaluationRequestEntity.setRelationName(instances.relationName());
        evaluationRequestEntity.setNumAttributes(instances.numAttributes());
        evaluationRequestEntity.setNumInstances(instances.numInstances());
        return evaluationRequestEntity;
    }

    @Override
    protected List<EvaluationRequest> prepareAndBuildRequests() {
        return classifierTestDataProvider.getTestDataModels()
                .stream()
                .map(this::createEvaluationRequest)
                .collect(Collectors.toList());
    }

    private EvaluationRequest createEvaluationRequest(ClassifierTestDataModel classifierTestDataModel) {
        Resource instancesResource = resolver.getResource(classifierTestDataModel.getTrainDataPath());
        Instances instances = instancesLoader.loadInstances(instancesResource);
        EvaluationRequest evaluationRequest = new EvaluationRequest();
        evaluationRequest.setData(instances);
        evaluationRequest.setEvaluationMethod(classifierTestDataModel.getEvaluationMethod());
        if (EvaluationMethod.CROSS_VALIDATION.equals(classifierTestDataModel.getEvaluationMethod())) {
            evaluationRequest.setNumFolds(classifierTestDataModel.getNumFolds());
            evaluationRequest.setNumTests(classifierTestDataModel.getNumTests());
            evaluationRequest.setSeed(classifierTestDataModel.getSeed());
        }
        return evaluationRequest;
    }
}
