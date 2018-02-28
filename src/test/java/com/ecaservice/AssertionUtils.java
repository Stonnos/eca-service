package com.ecaservice;

import org.assertj.core.api.Assertions;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Assertion utility class.
 *
 * @author Roman Batygin
 */

public class AssertionUtils {

    /**
     * Asserts that list has only one element.
     *
     * @param list list object
     * @param <T>  generic type
     */
    public static <T> void assertSingletonList(List<T> list) {
        Assertions.assertThat(CollectionUtils.isEmpty(list)).isFalse();
        Assertions.assertThat(list.size()).isEqualTo(1);
    }
}
