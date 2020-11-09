package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.classifier.options.config.ClassifiersOptionsConfig;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import eca.converters.model.ClassificationModel;
import eca.core.evaluation.EvaluationResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;

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

    private static final String MODEL_FILE_FORMAT = "%s_%s";

    private final ClassifiersOptionsConfig classifiersOptionsConfig;
    private final DataService dataService;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handles response from eca - server.
     *
     * @param ecaRequestEntity   - eca request entity
     * @param evaluationResponse - evaluation response
     */
    public void handleResponse(EcaRequestEntity ecaRequestEntity,
                               EvaluationResponse evaluationResponse) {
        try {
            ecaRequestEntity.setStatus(evaluationResponse.getStatus());
            if (TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus())) {
                EvaluationResults evaluationResults = evaluationResponse.getEvaluationResults();
                ClassificationModel classifierModel =
                        new ClassificationModel((AbstractClassifier) evaluationResults.getClassifier(),
                                evaluationResults.getEvaluation().getData(), evaluationResults.getEvaluation(),
                                classifiersOptionsConfig.getMaximumFractionDigits(),
                                evaluationResults.getClassifier().getClass().getSimpleName());
                String fileName =
                        String.format(MODEL_FILE_FORMAT, evaluationResults.getClassifier().getClass().getSimpleName(),
                                evaluationResponse.getRequestId());
                dataService.saveModel(classifierModel, fileName);
            }
            ecaRequestEntity.setRequestStage(RequestStageType.COMPLETED);
        } catch (Exception ex) {
            log.error("There was an error while response [{}] handling: {}", ecaRequestEntity.getCorrelationId(),
                    ex.getMessage(), ex);
            ecaRequestEntity.setRequestStage(RequestStageType.ERROR);
            ecaRequestEntity.setErrorMessage(ex.getMessage());
        } finally {
            ecaRequestEntity.setEndDate(LocalDateTime.now());
            ecaRequestRepository.save(ecaRequestEntity);
        }
    }
}
