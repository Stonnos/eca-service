package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.MessageError;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.classifier.options.config.ClassifiersOptionsConfig;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.core.model.ClassificationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Eca response handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EcaResponseHandler {

    private static final String MODEL_FILE_FORMAT = "%s_%s.model";

    private final ExternalApiConfig externalApiConfig;
    private final ClassifiersOptionsConfig classifiersOptionsConfig;
    private final FileDataService fileDataService;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handles response from eca - server.
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @param evaluationResponse      - evaluation response
     */
    public void handleResponse(EvaluationRequestEntity evaluationRequestEntity,
                               EvaluationResponse evaluationResponse) {
        log.info("Starting to process response with correlation id [{}]", evaluationRequestEntity.getCorrelationId());
        try {
            if (!TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus())) {
                evaluationRequestEntity.setRequestStage(RequestStageType.ERROR);
                String errorMessage = Optional.ofNullable(evaluationResponse.getErrors())
                        .map(messageErrors -> messageErrors.iterator().next())
                        .map(MessageError::getMessage)
                        .orElse(null);
                evaluationRequestEntity.setErrorMessage(errorMessage);
            } else {
                EvaluationResults evaluationResults = evaluationResponse.getEvaluationResults();
                saveEvaluationResults(evaluationResults, evaluationRequestEntity);
                saveClassifierToFile(evaluationResults, evaluationRequestEntity);
                evaluationRequestEntity.setRequestStage(RequestStageType.COMPLETED);
            }
            log.info("Response with correlation id [{}] has been processed",
                    evaluationRequestEntity.getCorrelationId());
        } catch (Exception ex) {
            log.error("There was an error while response [{}] handling: {}", evaluationRequestEntity.getCorrelationId(),
                    ex.getMessage(), ex);
            evaluationRequestEntity.setRequestStage(RequestStageType.ERROR);
            evaluationRequestEntity.setErrorMessage(ex.getMessage());
        } finally {
            evaluationRequestEntity.setEndDate(LocalDateTime.now());
            ecaRequestRepository.save(evaluationRequestEntity);
        }
    }

    private void saveEvaluationResults(EvaluationResults evaluationResults,
                                       EvaluationRequestEntity evaluationRequestEntity) {
        Evaluation evaluation = evaluationResults.getEvaluation();
        evaluationRequestEntity.setNumTestInstances((int) evaluation.numInstances());
        evaluationRequestEntity.setNumCorrect((int) evaluation.correct());
        evaluationRequestEntity.setNumIncorrect((int) evaluation.incorrect());
        evaluationRequestEntity.setPctCorrect(BigDecimal.valueOf(evaluation.pctCorrect()));
        evaluationRequestEntity.setPctIncorrect(BigDecimal.valueOf(evaluation.pctIncorrect()));
        evaluationRequestEntity.setMeanAbsoluteError(BigDecimal.valueOf(evaluation.meanAbsoluteError()));
    }

    private void saveClassifierToFile(EvaluationResults evaluationResults,
                                      EvaluationRequestEntity evaluationRequestEntity) throws Exception {
        ClassificationModel classifierModel =
                new ClassificationModel((AbstractClassifier) evaluationResults.getClassifier(),
                        evaluationResults.getEvaluation().getData(), evaluationResults.getEvaluation(),
                        classifiersOptionsConfig.getMaximumFractionDigits());
        String fileName =
                String.format(MODEL_FILE_FORMAT, evaluationResults.getClassifier().getClass().getSimpleName(),
                        evaluationRequestEntity.getCorrelationId());
        File classifierFile = new File(externalApiConfig.getClassifiersPath(), fileName);
        log.info("Starting to save model [{}] into file {}", evaluationRequestEntity.getCorrelationId(),
                classifierFile.getAbsolutePath());
        fileDataService.saveModel(classifierModel, classifierFile);
        log.info("Model [{}] has been saved into file {}", evaluationRequestEntity.getCorrelationId(),
                classifierFile.getAbsolutePath());
        evaluationRequestEntity.setClassifierAbsolutePath(classifierFile.getAbsolutePath());
    }
}
