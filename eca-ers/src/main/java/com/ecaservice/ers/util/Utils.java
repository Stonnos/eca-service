package com.ecaservice.ers.util;

import com.ecaservice.ers.dto.EvaluationMethod;
import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String CV_FORMAT = "%d - блочная кросс - проверка";
    private static final String CV_EXTENDED_FORMAT = "%d×%d - блочная кросс - проверка";

    /**
     * Returns integer value if specified big integer value is not null, null otherwise.
     *
     * @param value - big integer value
     * @return integer value
     */
    public static Integer toInteger(BigInteger value) {
        return Optional.ofNullable(value).map(BigInteger::intValue).orElse(null);
    }

    /**
     * Gets evaluation method description.
     *
     * @param evaluationMethod - evaluation method
     * @param numFolds         - num folds for k * V cross - validation method
     * @param numTests         - num tests for k * V cross - validation method
     * @return evaluation method description
     */
    public static String getEvaluationMethodDescription(EvaluationMethod evaluationMethod, Integer numFolds,
                                                        Integer numTests) {
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethod)) {
            return numTests == 1 ? String.format(CV_FORMAT, numFolds) :
                    String.format(CV_EXTENDED_FORMAT, numFolds, numTests);
        }
        return evaluationMethod.getDescription();
    }
}
