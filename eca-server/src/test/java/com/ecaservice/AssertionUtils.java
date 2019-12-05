package com.ecaservice;

import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Assertion utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class AssertionUtils {

    /**
     * Asserts that list has only one element.
     *
     * @param list list object
     * @param <T>  generic type
     */
    public static <T> void assertSingletonList(List<T> list) {
        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list).isNotEmpty();
        Assertions.assertThat(list.size()).isOne();
    }
}
