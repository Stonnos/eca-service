package com.ecaservice.mapping;

import com.ecaservice.model.entity.AbstractEvaluationEntity;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Abstract evaluation mapper.
 *
 * @author Roman Batygin
 */
public abstract class AbstractEvaluationMapper {

    private static final String GMT_TIME_ZONE = "GMT";

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter evaluationTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS");

    @Named("formatLocalDateTime")
    protected String formatLocalDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(dateTimeFormatter::format).orElse(null);
    }

    @Named("calculateEvaluationTotalTime")
    protected String calculateEvaluationTotalTime(AbstractEvaluationEntity evaluationEntity) {
        if (evaluationEntity.getStartDate() != null && evaluationEntity.getEndDate() != null) {
            long totalTimeMillis =
                    ChronoUnit.MILLIS.between(evaluationEntity.getStartDate(), evaluationEntity.getEndDate());
            LocalDateTime date =
                    Instant.ofEpochMilli(totalTimeMillis).atZone(ZoneId.of(GMT_TIME_ZONE)).toLocalDateTime();
            return evaluationTimeFormatter.format(date);
        }
        return null;
    }
}
