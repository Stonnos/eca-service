package com.ecaservice.external.api.test.service.runner;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.dto.AutoTestType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.model.EvaluationTestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.service.EvaluationTestDataService;
import com.ecaservice.external.api.test.service.JobService;
import com.ecaservice.external.api.test.service.TestWorkerService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Evaluation request auto test runner.
 *
 * @author Roman Batygin
 */
@Component
public class EvaluationRequestAutoTestRunner extends AbstractAutoTestRunner<AutoTestEntity, EvaluationTestDataModel> {

    private final EvaluationTestDataService evaluationTestDataService;

    /**
     * Constructor with parameters.
     *
     * @param externalApiTestsConfig    - external api config
     * @param jobService                - job service
     * @param testWorkerService         - test worker service
     * @param autoTestRepository        - auto test repository
     * @param evaluationTestDataService - evaluation test data service
     */
    public EvaluationRequestAutoTestRunner(ExternalApiTestsConfig externalApiTestsConfig,
                                           JobService jobService,
                                           TestWorkerService testWorkerService,
                                           AutoTestRepository autoTestRepository,
                                           EvaluationTestDataService evaluationTestDataService) {
        super(AutoTestType.EVALUATION_REQUEST_PROCESS, externalApiTestsConfig, jobService, testWorkerService,
                autoTestRepository);
        this.evaluationTestDataService = evaluationTestDataService;
    }

    @Override
    protected List<EvaluationTestDataModel> getTestDataModels() {
        return evaluationTestDataService.getTestDataModels();
    }

    @Override
    protected AutoTestEntity createAutoTestEntity(EvaluationTestDataModel testDataModel) {
        return new AutoTestEntity();
    }
}
