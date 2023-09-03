package com.ecaservice.external.api.test.util;

import com.ecaservice.external.api.dto.EvaluationResultsResponseDto;
import eca.core.evaluation.Evaluation;
import eca.core.model.ClassificationModel;
import lombok.experimental.UtilityClass;

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
