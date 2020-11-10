package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.classifier.options.config.ClassifiersOptionsConfig;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import eca.converters.model.ClassificationModel;
import eca.core.evaluation.EvaluationResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;

import java.io.File;
import java.time.LocalDateTime;

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
    private final DataService dataService;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handles response from eca - server.
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @param evaluationResponse      - evaluation response
     */
    public void handleResponse(EvaluationRequestEntity evaluationRequestEntity,
                               EvaluationResponse evaluationResponse) {
        try {
            if (!TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus())) {
                evaluationRequestEntity.setRequestStage(RequestStageType.ERROR);
            } else {
                EvaluationResults evaluationResults = evaluationResponse.getEvaluationResults();
                ClassificationModel classifierModel =
                        new ClassificationModel((AbstractClassifier) evaluationResults.getClassifier(),
                                evaluationResults.getEvaluation().getData(), evaluationResults.getEvaluation(),
                                classifiersOptionsConfig.getMaximumFractionDigits(),
                                evaluationResults.getClassifier().getClass().getSimpleName());
                String fileName =
                        String.format(MODEL_FILE_FORMAT, evaluationResults.getClassifier().getClass().getSimpleName(),
                                evaluationRequestEntity.getCorrelationId());
                File classifierFile = new File(externalApiConfig.getClassifiersPath(), fileName);
                log.debug("Starting to save model [{}] into file {}", evaluationRequestEntity.getCorrelationId(),
                        classifierFile.getAbsolutePath());
                dataService.saveModel(classifierModel, classifierFile);
                log.debug("Model [{}] has been saved into file {}", evaluationRequestEntity.getCorrelationId(),
                        classifierFile.getAbsolutePath());
                evaluationRequestEntity.setClassifierAbsolutePath(classifierFile.getAbsolutePath());
                evaluationRequestEntity.setRequestStage(RequestStageType.COMPLETED);
            }
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
}
