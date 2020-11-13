package com.ecaservice.external.api.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static com.ecaservice.external.api.util.Utils.isValidTrainData;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link Utils} class functionality.
 *
 * @author Roman Batygin
 */
class UtilsTest {

    private static final String DATA_CSV = "data.csv";
    private static final String DATA_ISO = "data.iso";

    @Test
    void testEmptyTrainData() {
        assertThat(isValidTrainData(StringUtils.EMPTY)).isFalse();
    }

    @Test
    void testInvalidTrainDataExtension() {
        assertThat(isValidTrainData(DATA_ISO)).isFalse();
    }

    @Test
    void testValidTrainDataExtension() {
        assertThat(isValidTrainData(DATA_CSV)).isTrue();
    }
}
