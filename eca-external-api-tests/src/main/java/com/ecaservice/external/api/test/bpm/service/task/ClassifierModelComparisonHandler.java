package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.MatchResult;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.service.ExternalApiService;
import com.ecaservice.external.api.test.service.TestResultsMatcher;
import eca.converters.model.ClassificationModel;
import eca.core.evaluation.Evaluation;
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

    private final ExternalApiService externalApiService;

    /**
     * Constructor with parameters.
     *
     * @param autoTestRepository - auto test repository bean
     * @param externalApiService - external api service bean
     */
    public ClassifierModelComparisonHandler(AutoTestRepository autoTestRepository,
                                            ExternalApiService externalApiService) {
        super(TaskType.COMPARE_CLASSIFIER_MODEL_RESULT, autoTestRepository);
        this.externalApiService = externalApiService;
    }

    @Override
    protected void compareAndMatchFields(DelegateExecution execution,
                                         AutoTestEntity autoTestEntity,
                                         TestResultsMatcher matcher) throws IOException {
        log.debug("Compare classifier model result for execution id [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        ResponseDto<EvaluationResponseDto> responseDto = getVariable(execution, API_RESPONSE,
                new ParameterizedTypeReference<ResponseDto<EvaluationResponseDto>>() {
                });
        log.debug("Starting to download model for test [{}]", autoTestEntity.getId());
        ClassificationModel classificationModel =
                externalApiService.downloadModel(responseDto.getPayload().getRequestId());
        log.debug("Classifier model has been downloaded for test [{}]", autoTestEntity.getId());
        //Compare and match classifier model fields
        compareAndMatchEvaluationFields(autoTestEntity, responseDto.getPayload(), classificationModel, matcher);
        log.debug("Comparison classifier model has been finished for execution id [{}], process key [{}]",
                execution.getId(), execution.getProcessBusinessKey());
    }

    private void compareAndMatchEvaluationFields(AutoTestEntity autoTestEntity,
                                                 EvaluationResponseDto evaluationResponseDto,
                                                 ClassificationModel classificationModel,
                                                 TestResultsMatcher matcher) {
        log.debug("Compare classifier model result for test [{}]", autoTestEntity.getId());
        BigDecimal expectedPctCorrect = getScaledValue(evaluationResponseDto,
                EvaluationResponseDto::getPctCorrect);
        BigDecimal actualPctCorrect = getScaledValue(classificationModel, Evaluation::pctCorrect);
        MatchResult pctCorrectMatchResult = matcher.compareAndMatch(expectedPctCorrect, actualPctCorrect);
        BigDecimal expectedPctIncorrect = getScaledValue(evaluationResponseDto,
                EvaluationResponseDto::getPctIncorrect);
        BigDecimal actualPctIncorrect = getScaledValue(classificationModel, Evaluation::pctIncorrect);
        MatchResult pctIncorrectMatchResult = matcher.compareAndMatch(expectedPctIncorrect, actualPctIncorrect);
        BigDecimal expectedMeanAbsoluteError = getScaledValue(evaluationResponseDto,
                EvaluationResponseDto::getMeanAbsoluteError);
        BigDecimal actualMeanAbsoluteError = getScaledValue(classificationModel, Evaluation::meanAbsoluteError);
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
