package com.ecaservice.common.web.util;

import org.junit.jupiter.api.Test;

import static com.ecaservice.common.web.util.MaskUtils.mask;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link MaskUtils} class.
 *
 * @author Roman Batygin
 */
class MaskUtilsTest {

    private static final String STRING_VALUE = "007009005";

    @Test
    void testMaskValue() {
        String masked = mask(STRING_VALUE);
        assertThat(masked).isEqualTo("0070*****");
    }

    @Test
    void testMaskShortValue() {
        String masked = mask("0005");
        assertThat(masked).isEqualTo("****");
    }
}
