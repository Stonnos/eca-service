package com.ecaservice.load.test.util;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import weka.core.Instances;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String GMT_TIME_ZONE = "GMT";
    private static final int SCALE = 2;

    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss:SS");

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
            EvaluationRequestEntity evaluationRequestEntity = new EvaluationRequestEntity();
            evaluationRequestEntity.setCorrelationId(UUID.randomUUID().toString());
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

    /**
     * Gets total time between two dates in format HH:mm:ss:SS.
     *
     * @param start - start date
     * @param end   - end date
     * @return total time string
     */
    public static String totalTime(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            long totalTimeMillis = ChronoUnit.MILLIS.between(start, end);
            LocalDateTime totalTime =
                    Instant.ofEpochMilli(totalTimeMillis).atZone(ZoneId.of(GMT_TIME_ZONE)).toLocalDateTime();
            return TIME_FORMATTER.format(totalTime);
        }
        return null;
    }

    /**
     * Calculates tps value.
     *
     * @param loadTestEntity - load tests entity
     * @return tps value
     */
    public static BigDecimal tps(LoadTestEntity loadTestEntity) {
        if (loadTestEntity.getStarted() != null && loadTestEntity.getFinished() != null) {
            long totalTimeSec = ChronoUnit.SECONDS.between(loadTestEntity.getStarted(), loadTestEntity.getFinished());
            if (totalTimeSec > 0) {
                return BigDecimal.valueOf(loadTestEntity.getNumRequests()).divide(BigDecimal.valueOf(totalTimeSec),
                        SCALE, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf(loadTestEntity.getNumRequests());
            }
        } else {
            return null;
        }
    }
}
