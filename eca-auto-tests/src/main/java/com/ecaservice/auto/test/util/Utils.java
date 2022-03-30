package com.ecaservice.auto.test.util;

import com.ecaservice.auto.test.projections.TestResultProjection;
import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.MessageError;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.test.common.model.TestResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Converts classifier options to json string.
     *
     * @param classifierOptions - classifier options
     * @return classifier options as json
     */
    public static String toJson(ClassifierOptions classifierOptions) {
        try {
            return OBJECT_MAPPER.writeValueAsString(classifierOptions);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Transform double value to scaled big decimal. Returns {@code null} in case if double value is NaN.
     *
     * @param value - double value
     * @param scale - scale value
     * @return big decimal value
     */
    public static BigDecimal getScaledValue(double value, int scale) {
        if (weka.core.Utils.isMissingValue(value)) {
            return null;
        }
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * Gets scaled decimal value.
     *
     * @param decimal - big decimal value
     * @param scale   - scale value
     * @return scaled decimal value
     */
    public static BigDecimal getScaledValue(BigDecimal decimal, int scale) {
        return Optional.ofNullable(decimal)
                .map(d -> d.setScale(scale, RoundingMode.HALF_UP))
                .orElse(null);
    }

    /**
     * Gets first response error message.
     *
     * @param ecaResponse - eca response
     * @return error message
     */
    public static String getFirstErrorMessage(EcaResponse ecaResponse) {
        return Optional.ofNullable(ecaResponse.getErrors())
                .map(messageErrors -> messageErrors.iterator().next())
                .map(MessageError::getMessage)
                .orElse(null);
    }

    /**
     * Calculates final test result.
     *
     * @param testResults - test results
     * @return test result
     */
    public static TestResult calculateFinalTestResult(List<TestResultProjection> testResults) {
        if (testResults.stream().allMatch(testResult -> TestResult.PASSED.equals(testResult.getTestResult()))) {
            return TestResult.PASSED;
        }
        return TestResult.FAILED;
    }

    /**
     * Calculates sum for specified field.
     *
     * @param testResults - test results
     * @param function    - field function
     * @return sum value
     */
    public static int sum(List<TestResultProjection> testResults, ToIntFunction<TestResultProjection> function) {
        return testResults.stream()
                .mapToInt(function)
                .sum();
    }
}
