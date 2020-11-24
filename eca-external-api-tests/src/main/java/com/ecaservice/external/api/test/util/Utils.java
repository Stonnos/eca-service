package com.ecaservice.external.api.test.util;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.ResponseDto;
import eca.converters.model.ClassificationModel;
import eca.core.evaluation.Evaluation;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Function;

import static com.ecaservice.external.api.test.entity.Constraints.SCALE;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String GMT_TIME_ZONE = "GMT";

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss:SS");

    /**
     * Copy resource into byte array.
     *
     * @param resource - resource object
     * @return byte array
     * @throws IOException in case of I/O error
     */
    public static byte[] copyToByteArray(Resource resource) throws IOException {
        @Cleanup InputStream inputStream = resource.getInputStream();
        @Cleanup ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Gets value safe.
     *
     * @param responseDto   - response dto
     * @param valueFunction - value function
     * @param <P>           - payload type
     * @param <V>           - value type
     * @return value
     */
    public static <P, V> V getValueSafe(ResponseDto<P> responseDto, Function<P, V> valueFunction) {
        return Optional.ofNullable(responseDto)
                .map(ResponseDto::getPayload)
                .map(valueFunction)
                .orElse(null);
    }

    /**
     * Gets scaled decimal value.
     *
     * @param evaluationResponseDto - evaluation response
     * @param valueFunction         value function
     * @return scaled decimal value
     */
    public static BigDecimal getScaledValue(EvaluationResponseDto evaluationResponseDto,
                                            Function<EvaluationResponseDto, BigDecimal> valueFunction) {
        return Optional.ofNullable(evaluationResponseDto)
                .map(valueFunction)
                .map(decimal -> decimal.setScale(SCALE, RoundingMode.HALF_UP))
                .orElse(null);
    }

    /**
     * Gets scaled decimal value.
     *
     * @param classificationModel - classification model
     * @param valueFunction       value function
     * @return scaled decimal value
     */
    public static BigDecimal getScaledValue(ClassificationModel classificationModel,
                                            Function<Evaluation, Double> valueFunction) {
        return Optional.ofNullable(classificationModel)
                .map(ClassificationModel::getEvaluation)
                .map(valueFunction)
                .map(BigDecimal::new)
                .map(decimal -> decimal.setScale(SCALE, RoundingMode.HALF_UP))
                .orElse(null);
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
}
