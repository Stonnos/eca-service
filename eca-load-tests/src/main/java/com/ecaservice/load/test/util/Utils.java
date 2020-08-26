package com.ecaservice.load.test.util;

import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    /**
     * Creates evaluation request entity.
     *
     * @param loadTestEntity - load test entity
     * @return evaluation request entity
     */
    public static EvaluationRequestEntity createEvaluationRequestEntity(LoadTestEntity loadTestEntity) {
        String correlationId = UUID.randomUUID().toString();
        EvaluationRequestEntity evaluationRequestEntity = new EvaluationRequestEntity();
        evaluationRequestEntity.setCorrelationId(correlationId);
        evaluationRequestEntity.setLoadTestEntity(loadTestEntity);
        return evaluationRequestEntity;
    }
}
