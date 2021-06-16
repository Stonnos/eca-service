package com.ecaservice.core.filter.util;

import com.ecaservice.core.filter.entity.FilterField;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ReflectionUtils} class.
 *
 * @author Roman Batygin
 */
class ReflectionUtilsTest {

    private static final String FILTER_FILED_DICTIONARY_NAME = "dictionary.name";

    @Test
    void testGetGetterReturnType() {
        Class<?> actual = ReflectionUtils.getFieldType(FILTER_FILED_DICTIONARY_NAME, FilterField.class);
        Assertions.assertThat(actual).isAssignableFrom(String.class);
    }
}
