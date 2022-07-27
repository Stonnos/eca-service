package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.config.ClassifiersOptionsConfig;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.core.model.ClassificationModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import weka.classifiers.AbstractClassifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.ecaservice.external.api.util.Utils.toJson;

/**
 * Eca response handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EcaResponseHandler {

    private static final String MODEL_PATH_FORMAT = "classifier-%s.model";

    private final ExternalApiConfig externalApiConfig;
    private final ClassifiersOptionsConfig classifiersOptionsConfig;
    private final ObjectStorageService objectStorageService;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
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
                Optional.ofNullable(evaluationResponse.getErrors())
                        .map(messageErrors -> messageErrors.iterator().next())
                        .ifPresent(error -> {
                            evaluationRequestEntity.setErrorCode(error.getCode());
                            evaluationRequestEntity.setErrorMessage(error.getMessage());
                        });
            } else {
                EvaluationResults evaluationResults = evaluationResponse.getEvaluationResults();
                if (evaluationRequestEntity.isUseOptimalClassifierOptions()) {
                    populateEvaluationOptions(evaluationResults, evaluationRequestEntity);
                }
                saveEvaluationResults(evaluationResults, evaluationRequestEntity);
                uploadModelToS3(evaluationResults, evaluationRequestEntity);
                generateClassifierModelDownloadUrl(evaluationRequestEntity);
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

    private void populateEvaluationOptions(EvaluationResults evaluationResults,
                                           EvaluationRequestEntity evaluationRequestEntity) {
        var classifier = evaluationResults.getClassifier();
        Assert.isInstanceOf(AbstractClassifier.class, classifier,
                String.format("Got [%s] classifier class. Expected [%s] classifier sub class",
                        AbstractClassifier.class.getSimpleName(), classifier.getClass().getSimpleName()));
        var classifierOptions = classifierOptionsAdapter.convert((AbstractClassifier) classifier);
        evaluationRequestEntity.setClassifierOptionsJson(toJson(classifierOptions));
        Evaluation evaluation = evaluationResults.getEvaluation();
        if (!evaluation.isKCrossValidationMethod()) {
            evaluationRequestEntity.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        } else {
            evaluationRequestEntity.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
            evaluationRequestEntity.setNumFolds(evaluation.numFolds());
            evaluationRequestEntity.setNumTests(evaluation.getNumTests());
            evaluationRequestEntity.setSeed(evaluation.getSeed());
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

    private void uploadModelToS3(EvaluationResults evaluationResults,
                                 EvaluationRequestEntity evaluationRequestEntity) throws Exception {
        var classifierModel = buildClassificationModel(evaluationResults);
        String classifierPath = String.format(MODEL_PATH_FORMAT, evaluationRequestEntity.getCorrelationId());
        log.info("Starting to save model [{}] to S3 {}", evaluationRequestEntity.getCorrelationId(), classifierPath);
        objectStorageService.uploadObject(classifierModel, classifierPath);
        log.info("Model [{}] has been saved into file {}", evaluationRequestEntity.getCorrelationId(), classifierPath);
        evaluationRequestEntity.setClassifierPath(classifierPath);
    }

    private void generateClassifierModelDownloadUrl(EvaluationRequestEntity evaluationRequestEntity) {
        String classifierDownloadUrl =
                getClassifierModelDownloadPresignedUrl(evaluationRequestEntity.getClassifierPath());
        evaluationRequestEntity.setClassifierDownloadUrl(classifierDownloadUrl);
    }

    private ClassificationModel buildClassificationModel(EvaluationResults evaluationResults) {
        var classifier = (AbstractClassifier) evaluationResults.getClassifier();
        return new ClassificationModel(classifier, evaluationResults.getEvaluation().getData(),
                evaluationResults.getEvaluation(), classifiersOptionsConfig.getMaximumFractionDigits());
    }


    private String getClassifierModelDownloadPresignedUrl(String classifierPath) {
        return objectStorageService.getObjectPresignedProxyUrl(
                GetPresignedUrlObject.builder()
                        .objectPath(classifierPath)
                        .expirationTime(externalApiConfig.getClassifierDownloadUrlExpirationDays())
                        .expirationTimeUnit(TimeUnit.DAYS)
                        .build()
        );
    }
}
