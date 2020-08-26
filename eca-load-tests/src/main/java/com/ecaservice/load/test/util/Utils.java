package com.ecaservice.load.test.util;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import weka.core.Instances;

import java.util.UUID;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Creates evaluation request entity.
     *
     * @param loadTestEntity    - load test entity
     * @param classifierOptions - classifier options
     * @param instances         - instances object
     * @return evaluation request entity
     */
    public static EvaluationRequestEntity createEvaluationRequestEntity(LoadTestEntity loadTestEntity,
                                                                        ClassifierOptions classifierOptions,
                                                                        Instances instances) {
        try {
            String correlationId = UUID.randomUUID().toString();
            EvaluationRequestEntity evaluationRequestEntity = new EvaluationRequestEntity();
            evaluationRequestEntity.setCorrelationId(correlationId);
            evaluationRequestEntity.setLoadTestEntity(loadTestEntity);
            evaluationRequestEntity.setClassifierOptions(OBJECT_MAPPER.writeValueAsString(classifierOptions));
            evaluationRequestEntity.setRelationName(instances.relationName());
            evaluationRequestEntity.setNumAttributes(instances.numAttributes());
            evaluationRequestEntity.setNumInstances(instances.numInstances());
            return evaluationRequestEntity;
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
