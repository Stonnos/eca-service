package com.ecaservice.util;

import org.junit.jupiter.api.Test;

import static com.ecaservice.util.RangeUtils.formatDateRange;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RangeUtils} class.
 *
 * @author Roman Batygin
 */
public class RangeUtilsTest {

    private static final String LOWER_BOUND_VALUE = "2018-01-01";
    private static final String UPPER_BOUND_VALUE = "2019-01-01";

    private static final String DATE_RANGE_FORMAT = "с %s по %s";
    private static final String DATE_RANGE_WITH_LOWER_BOUND_FORMAT = "с %s";
    private static final String DATE_RANGE_WITH_UPPER_BOUND_FORMAT = "по %s";

    @Test
    public void testFullEnclosedDateRange() {
        String expected = String.format(DATE_RANGE_FORMAT, LOWER_BOUND_VALUE, UPPER_BOUND_VALUE);
        String actual = formatDateRange(LOWER_BOUND_VALUE, UPPER_BOUND_VALUE);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testLowerBoundDateRange() {
        String expected = String.format(DATE_RANGE_WITH_LOWER_BOUND_FORMAT, LOWER_BOUND_VALUE);
        String actual = formatDateRange(LOWER_BOUND_VALUE, null);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testUpperBoundDateRange() {
        String expected = String.format(DATE_RANGE_WITH_UPPER_BOUND_FORMAT, UPPER_BOUND_VALUE);
        String actual = formatDateRange(null, UPPER_BOUND_VALUE);
        assertThat(actual).isEqualTo(expected);
    }
}
