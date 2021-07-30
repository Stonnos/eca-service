package com.ecaservice.load.test.util;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.entity.RequestStageType;
import com.ecaservice.test.common.model.TestResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import weka.core.Instances;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final int SCALE = 2;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int THOUSAND = 1000;

    /**
     * Creates evaluation request entity.
     *
     * @param loadTestEntity    - load test entity
     * @param classifierOptions - classifier options
     * @param evaluationRequest - evaluation request object
     * @return evaluation request entity
     */
    public static EvaluationRequestEntity createEvaluationRequestEntity(LoadTestEntity loadTestEntity,
                                                                        ClassifierOptions classifierOptions,
                                                                        EvaluationRequest evaluationRequest) {
        try {
            EvaluationRequestEntity evaluationRequestEntity = new EvaluationRequestEntity();
            evaluationRequestEntity.setCorrelationId(UUID.randomUUID().toString());
            evaluationRequestEntity.setStageType(RequestStageType.NOT_SEND);
            evaluationRequestEntity.setTestResult(TestResult.UNKNOWN);
            evaluationRequestEntity.setLoadTestEntity(loadTestEntity);
            evaluationRequestEntity.setClassifierOptions(OBJECT_MAPPER.writeValueAsString(classifierOptions));
            Instances instances = evaluationRequest.getData();
            evaluationRequestEntity.setRelationName(instances.relationName());
            evaluationRequestEntity.setNumAttributes(instances.numAttributes());
            evaluationRequestEntity.setNumInstances(instances.numInstances());
            evaluationRequestEntity.setClassifierName(evaluationRequest.getClassifier().getClass().getSimpleName());
            return evaluationRequestEntity;
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Calculates tps value.
     *
     * @param loadTestEntity - load tests entity
     * @return tps value
     */
    public static BigDecimal tps(LoadTestEntity loadTestEntity) {
        if (loadTestEntity.getStarted() != null && loadTestEntity.getFinished() != null) {
            long totalTimeMillis = ChronoUnit.MILLIS.between(loadTestEntity.getStarted(), loadTestEntity.getFinished());
            BigDecimal totalTimeSeconds = BigDecimal.valueOf(totalTimeMillis)
                    .divide(BigDecimal.valueOf(THOUSAND), SCALE, RoundingMode.HALF_UP);
            if (totalTimeMillis > 0) {
                return BigDecimal.valueOf(loadTestEntity.getNumRequests())
                        .divide(totalTimeSeconds, SCALE, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf(loadTestEntity.getNumRequests());
            }
        } else {
            return null;
        }
    }
}
