package com.ecaservice.external.api.test.util;

import com.ecaservice.external.api.dto.EvaluationResultsResponseDto;
import com.ecaservice.external.api.dto.ResponseDto;
import eca.core.evaluation.Evaluation;
import eca.core.model.ClassificationModel;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.function.Function;

import static com.ecaservice.external.api.test.util.Constraints.SCALE;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

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
     * @param evaluationResultsResponseDto - evaluation response
     * @param valueFunction                - value function
     * @return scaled decimal value
     */
    public static BigDecimal getScaledValue(EvaluationResultsResponseDto evaluationResultsResponseDto,
                                            Function<EvaluationResultsResponseDto, BigDecimal> valueFunction) {
        return Optional.ofNullable(evaluationResultsResponseDto)
                .map(valueFunction)
                .map(decimal -> decimal.setScale(SCALE, RoundingMode.HALF_UP))
                .orElse(null);
    }

    /**
     * Gets scaled decimal value.
     *
     * @param classificationModel - classification model
     * @param valueFunction       - value function
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
}
