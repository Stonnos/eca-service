package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Abstract evaluation mapper.
 *
 * @author Roman Batygin
 */
public abstract class AbstractEvaluationMapper {

    private static final String GMT_TIME_ZONE = "GMT";

    private final DateTimeFormatter evaluationTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    @Named("calculateEvaluationTotalTime")
    protected String calculateEvaluationTotalTime(AbstractEvaluationEntity evaluationEntity) {
        if (evaluationEntity.getEvaluationTimeMillis() != null) {
            LocalDateTime totalTime = Instant.ofEpochMilli(evaluationEntity.getEvaluationTimeMillis())
                    .atZone(ZoneId.of(GMT_TIME_ZONE))
                    .toLocalDateTime();
            return evaluationTimeFormatter.format(totalTime);
        }
        return null;
    }
}
