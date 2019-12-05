package com.ecaservice.util;

import com.ecaservice.model.entity.EvaluationLog;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Unit tests for {@link ReflectionUtils} class.
 *
 * @author Roman Batygin
 */
public class ReflectionUtilsTest {

    private static final String INSTANCES_INFO_RELATION_NAME = "instancesInfo.relationName";

    @Test
    public void testGetGetterReturnType() {
        Class<?> actual = ReflectionUtils.getGetterReturnType(INSTANCES_INFO_RELATION_NAME, EvaluationLog.class);
        Assertions.assertThat(actual).isAssignableFrom(String.class);
    }
}
