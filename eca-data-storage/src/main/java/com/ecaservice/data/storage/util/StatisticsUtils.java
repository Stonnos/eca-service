package com.ecaservice.data.storage.util;

import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Statistics utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class StatisticsUtils {

    /**
     * Checks that value belongs to specified interval.
     *
     * @param left         - lower bound
     * @param right        - upper bound
     * @param val          - value
     * @param includeLeft  - include left bound?
     * @param includeRight - include right bound?
     * @return {@code true} if value belongs to specified interval, otherwise {@code false}
     */
    public static boolean contains(double left, double right, double val,
                                   boolean includeLeft,
                                   boolean includeRight) {
        if (includeLeft && includeRight) {
            return val >= left && val <= right;
        } else if (includeLeft) {
            return val >= left && val < right;
        } else if (includeRight) {
            return val > left && val <= right;
        } else {
            return val > left && val < right;
        }
    }

    /**
     * Calculates mean value for specified array.
     *
     * @param values - values array
     * @return mean value
     */
    public static double mean(List<Double> values) {
        return values.stream()
                .mapToDouble(value -> value)
                .average()
                .orElse(Double.NaN);
    }

    /**
     * Calculates min value for specified array.
     *
     * @param values - values array
     * @return min value
     */
    public static double min(List<Double> values) {
        return values.stream()
                .mapToDouble(value -> value)
                .min()
                .orElse(Double.NaN);
    }

    /**
     * Calculates max value for specified array.
     *
     * @param values - values array
     * @return max value
     */
    public static double max(List<Double> values) {
        return values.stream()
                .mapToDouble(value -> value)
                .max()
                .orElse(Double.NaN);
    }

    /**
     * Calculates variance value for specified array.
     *
     * @param values - values array
     * @return variance value
     */
    public static double variance(List<Double> values, double meanValue) {
        double squareSum = values.stream()
                .mapToDouble(value -> (value - meanValue) * (value - meanValue))
                .sum();
        return squareSum / (values.size() - 1);
    }
}
