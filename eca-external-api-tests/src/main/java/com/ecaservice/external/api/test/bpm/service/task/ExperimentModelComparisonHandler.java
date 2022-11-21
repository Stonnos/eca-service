package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.dto.ExperimentResultsResponseDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.entity.ExperimentRequestAutoTestEntity;
import com.ecaservice.external.api.test.repository.ExperimentRequestAutoTestRepository;
import com.ecaservice.external.api.test.service.ExternalApiService;
import com.ecaservice.test.common.model.MatchResult;
import com.ecaservice.test.common.service.TestResultsMatcher;
import eca.dataminer.AbstractExperiment;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.API_RESPONSE;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

/**
 * Implements handler to compare downloaded experiment model result.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentModelComparisonHandler extends ComparisonTaskHandler {

    private static final ParameterizedTypeReference<ResponseDto<ExperimentResultsResponseDto>>
            API_RESPONSE_TYPE_REFERENCE =
            new ParameterizedTypeReference<ResponseDto<ExperimentResultsResponseDto>>() {
            };

    private final ExternalApiTestsConfig externalApiTestsConfig;
    private final ExternalApiService externalApiService;
    private final ExperimentRequestAutoTestRepository experimentRequestAutoTestRepository;

    /**
     * Constructor with parameters.
     *
     * @param externalApiTestsConfig              - external api tests config
     * @param externalApiService                  - external api service bean
     * @param experimentRequestAutoTestRepository - experiment request auto test repository
     */
    public ExperimentModelComparisonHandler(
            ExternalApiTestsConfig externalApiTestsConfig,
            ExternalApiService externalApiService,
            ExperimentRequestAutoTestRepository experimentRequestAutoTestRepository) {
        super(TaskType.COMPARE_EXPERIMENT_MODEL_RESULT);
        this.externalApiTestsConfig = externalApiTestsConfig;
        this.externalApiService = externalApiService;
        this.experimentRequestAutoTestRepository = experimentRequestAutoTestRepository;
    }

    @Override
    protected void compareAndMatchFields(DelegateExecution execution,
                                         Long autoTestId,
                                         TestResultsMatcher matcher) throws IOException {
        log.debug("Compare experiment model result for execution id [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        var autoTestEntity = experimentRequestAutoTestRepository.findById(autoTestId)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentRequestAutoTestEntity.class, autoTestId));
        var responseDto = getVariable(execution, API_RESPONSE, API_RESPONSE_TYPE_REFERENCE);
        log.debug("Starting to download experiment model for test [{}]", autoTestEntity.getId());
        AbstractExperiment<?> experiment =
                externalApiService.downloadExperiment(responseDto.getPayload().getExperimentModelUrl());
        log.debug("Experiment model has been downloaded for test [{}]", autoTestEntity.getId());
        //Compare and match experiment model fields
        compareAndMatchExperimentFields(autoTestEntity, experiment, matcher);
        experimentRequestAutoTestRepository.save(autoTestEntity);
        log.debug("Comparison experiment model has been finished for execution id [{}], process key [{}]",
                execution.getId(), execution.getProcessBusinessKey());
    }

    private void compareAndMatchExperimentFields(ExperimentRequestAutoTestEntity autoTestEntity,
                                                 AbstractExperiment<?> experiment,
                                                 TestResultsMatcher matcher) {
        log.debug("Compare experiment model result for test [{}]", autoTestEntity.getId());
        int expectedNumModels = externalApiTestsConfig.getExpectedExperimentNumModels();
        int actualNumModels = Optional.ofNullable(experiment.getHistory()).map(List::size).orElse(0);
        MatchResult numModelsMatchResult = matcher.compareAndMatch(expectedNumModels, actualNumModels);

        autoTestEntity.setExpectedNumModels(expectedNumModels);
        autoTestEntity.setActualNumModels(actualNumModels);
        autoTestEntity.setNumModelsMatchResult(numModelsMatchResult);
    }
}
