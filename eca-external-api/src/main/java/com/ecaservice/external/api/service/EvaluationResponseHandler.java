package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.config.ClassifiersOptionsConfig;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.mapping.EcaRequestMapper;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.core.model.ClassificationModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import weka.classifiers.AbstractClassifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static com.ecaservice.external.api.util.Utils.toJson;

/**
 * Evaluation response handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationResponseHandler extends AbstractEcaResponseHandler<EvaluationRequestEntity, EvaluationResponse> {

    private static final String MODEL_PATH_FORMAT = "classifier-%s.model";

    private final ExternalApiConfig externalApiConfig;
    private final ClassifiersOptionsConfig classifiersOptionsConfig;
    private final ObjectStorageService objectStorageService;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;

    /**
     * Constructor with parameters.
     *
     * @param ecaRequestRepository     - eca request repository
     * @param requestStageHandler      - request stage handler
     * @param ecaRequestMapper         - eca request mapper
     * @param externalApiConfig        - external api config
     * @param classifiersOptionsConfig - classifiers options config
     * @param objectStorageService     - object storage service
     * @param classifierOptionsAdapter - classifier options adapter
     */
    public EvaluationResponseHandler(EcaRequestRepository ecaRequestRepository,
                                     RequestStageHandler requestStageHandler,
                                     EcaRequestMapper ecaRequestMapper,
                                     ExternalApiConfig externalApiConfig,
                                     ClassifiersOptionsConfig classifiersOptionsConfig,
                                     ObjectStorageService objectStorageService,
                                     ClassifierOptionsAdapter classifierOptionsAdapter) {
        super(ecaRequestRepository, requestStageHandler, ecaRequestMapper);
        this.externalApiConfig = externalApiConfig;
        this.classifiersOptionsConfig = classifiersOptionsConfig;
        this.objectStorageService = objectStorageService;
        this.classifierOptionsAdapter = classifierOptionsAdapter;
    }

    @Override
    protected void internalHandleSuccessResponse(EvaluationRequestEntity requestEntity,
                                                 EvaluationResponse ecaResponse) {
        log.info("Starting to process success evaluation response [{}]", requestEntity.getCorrelationId());
        EvaluationResults evaluationResults = ecaResponse.getEvaluationResults();
        if (requestEntity.isUseOptimalClassifierOptions()) {
            populateEvaluationOptions(evaluationResults, requestEntity);
        }
        saveEvaluationResults(evaluationResults, requestEntity);
        uploadModelToS3(evaluationResults, requestEntity);
        generateClassifierModelDownloadUrl(requestEntity);
        log.info("Success evaluation response [{}] has been processed", requestEntity.getCorrelationId());
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
                                 EvaluationRequestEntity evaluationRequestEntity) {
        try {
            var classifierModel = buildClassificationModel(evaluationResults);
            String classifierPath = String.format(MODEL_PATH_FORMAT, evaluationRequestEntity.getCorrelationId());
            log.info("Starting to save model [{}] to S3 {}", evaluationRequestEntity.getCorrelationId(),
                    classifierPath);
            objectStorageService.uploadObject(classifierModel, classifierPath);
            log.info("Model [{}] has been saved into file {}", evaluationRequestEntity.getCorrelationId(),
                    classifierPath);
            evaluationRequestEntity.setClassifierPath(classifierPath);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
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
