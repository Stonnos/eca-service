package com.ecaservice.external.api.test.service.runner;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.dto.AutoTestType;
import com.ecaservice.external.api.test.entity.ExperimentRequestAutoTestEntity;
import com.ecaservice.external.api.test.model.ExperimentTestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.service.ExperimentTestDataService;
import com.ecaservice.external.api.test.service.JobService;
import com.ecaservice.external.api.test.service.TestWorkerService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Experiment request auto test runner.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentRequestAutoTestRunner
        extends AbstractAutoTestRunner<ExperimentRequestAutoTestEntity, ExperimentTestDataModel> {

    private final ExperimentTestDataService experimentTestDataService;

    /**
     * Constructor with parameters.
     *
     * @param externalApiTestsConfig    - external api config
     * @param jobService                - job service
     * @param testWorkerService         - test worker service
     * @param autoTestRepository        - auto test repository
     * @param experimentTestDataService - evaluation test data service
     */
    public ExperimentRequestAutoTestRunner(ExternalApiTestsConfig externalApiTestsConfig,
                                           JobService jobService,
                                           TestWorkerService testWorkerService,
                                           AutoTestRepository autoTestRepository,
                                           ExperimentTestDataService experimentTestDataService) {
        super(AutoTestType.EXPERIMENT_REQUEST_PROCESS, externalApiTestsConfig, jobService, testWorkerService,
                autoTestRepository);
        this.experimentTestDataService = experimentTestDataService;
    }

    @Override
    protected List<ExperimentTestDataModel> getTestDataModels() {
        return experimentTestDataService.getTestDataModels();
    }

    @Override
    protected ExperimentRequestAutoTestEntity createAutoTestEntity(ExperimentTestDataModel testDataModel) {
        return new ExperimentRequestAutoTestEntity();
    }
}
