package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.dto.EvaluationResultsResponseDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.EvaluationRequestAutoTestEntity;
import com.ecaservice.external.api.test.repository.EvaluationRequestAutoTestRepository;
import com.ecaservice.external.api.test.service.ExternalApiService;
import com.ecaservice.test.common.model.MatchResult;
import com.ecaservice.test.common.service.TestResultsMatcher;
import eca.core.evaluation.Evaluation;
import eca.core.model.ClassificationModel;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.API_RESPONSE;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;
import static com.ecaservice.external.api.test.util.Utils.getScaledValue;

/**
 * Implements handler to compare downloaded classifier model result.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ClassifierModelComparisonHandler extends ComparisonTaskHandler {

    private static final ParameterizedTypeReference<ResponseDto<EvaluationResultsResponseDto>>
            API_RESPONSE_TYPE_REFERENCE =
            new ParameterizedTypeReference<ResponseDto<EvaluationResultsResponseDto>>() {
            };

    private final ExternalApiService externalApiService;
    private final EvaluationRequestAutoTestRepository evaluationRequestAutoTestRepository;

    /**
     * Constructor with parameters.
     *
     * @param externalApiService                  - external api service bean
     * @param evaluationRequestAutoTestRepository - evaluation request auto test repository
     */
    public ClassifierModelComparisonHandler(ExternalApiService externalApiService,
                                            EvaluationRequestAutoTestRepository evaluationRequestAutoTestRepository) {
        super(TaskType.COMPARE_CLASSIFIER_MODEL_RESULT);
        this.externalApiService = externalApiService;
        this.evaluationRequestAutoTestRepository = evaluationRequestAutoTestRepository;
    }

    @Override
    protected void compareAndMatchFields(DelegateExecution execution,
                                         Long autoTestId,
                                         TestResultsMatcher matcher) throws IOException {
        log.debug("Compare classifier model result for execution id [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        var autoTestEntity = evaluationRequestAutoTestRepository.findById(autoTestId)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationRequestAutoTestEntity.class, autoTestId));
        var responseDto = getVariable(execution, API_RESPONSE, API_RESPONSE_TYPE_REFERENCE);
        log.debug("Starting to download model for test [{}]", autoTestEntity.getId());
        ClassificationModel classificationModel =
                externalApiService.downloadModel(responseDto.getPayload().getModelUrl());
        log.debug("Classifier model has been downloaded for test [{}]", autoTestEntity.getId());
        //Compare and match classifier model fields
        compareAndMatchEvaluationFields(autoTestEntity, responseDto.getPayload(), classificationModel, matcher);
        evaluationRequestAutoTestRepository.save(autoTestEntity);
        log.debug("Comparison classifier model has been finished for execution id [{}], process key [{}]",
                execution.getId(), execution.getProcessBusinessKey());
    }

    private void compareAndMatchEvaluationFields(EvaluationRequestAutoTestEntity autoTestEntity,
                                                 EvaluationResultsResponseDto evaluationResultsResponseDto,
                                                 ClassificationModel classificationModel,
                                                 TestResultsMatcher matcher) {
        log.debug("Compare classifier model result for test [{}]", autoTestEntity.getId());
        BigDecimal expectedPctCorrect = getScaledValue(classificationModel, Evaluation::pctCorrect);
        BigDecimal actualPctCorrect =
                getScaledValue(evaluationResultsResponseDto, EvaluationResultsResponseDto::getPctCorrect);
        MatchResult pctCorrectMatchResult = matcher.compareAndMatch(expectedPctCorrect, actualPctCorrect);
        BigDecimal expectedPctIncorrect = getScaledValue(classificationModel, Evaluation::pctIncorrect);
        BigDecimal actualPctIncorrect =
                getScaledValue(evaluationResultsResponseDto, EvaluationResultsResponseDto::getPctIncorrect);
        MatchResult pctIncorrectMatchResult = matcher.compareAndMatch(expectedPctIncorrect, actualPctIncorrect);
        BigDecimal expectedMeanAbsoluteError = getScaledValue(classificationModel, Evaluation::meanAbsoluteError);
        BigDecimal actualMeanAbsoluteError =
                getScaledValue(evaluationResultsResponseDto, EvaluationResultsResponseDto::getMeanAbsoluteError);
        MatchResult meanAbsoluteErrorMatchResult =
                matcher.compareAndMatch(expectedMeanAbsoluteError, actualMeanAbsoluteError);

        autoTestEntity.setExpectedPctCorrect(expectedPctCorrect);
        autoTestEntity.setActualPctCorrect(actualPctCorrect);
        autoTestEntity.setPctCorrectMatchResult(pctCorrectMatchResult);
        autoTestEntity.setExpectedPctIncorrect(expectedPctIncorrect);
        autoTestEntity.setActualPctIncorrect(actualPctIncorrect);
        autoTestEntity.setPctIncorrectMatchResult(pctIncorrectMatchResult);
        autoTestEntity.setExpectedMeanAbsoluteError(expectedMeanAbsoluteError);
        autoTestEntity.setActualMeanAbsoluteError(actualMeanAbsoluteError);
        autoTestEntity.setMeanAbsoluteErrorMatchResult(meanAbsoluteErrorMatchResult);
    }
}
