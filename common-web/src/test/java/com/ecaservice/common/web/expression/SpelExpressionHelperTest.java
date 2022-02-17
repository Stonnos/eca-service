package com.ecaservice.common.web.expression;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SpelExpressionHelper} class.
 *
 * @author Roman Batygin
 */
class SpelExpressionHelperTest {

    private static final String INPUT_VALUE_1 = "inputValue1";
    private static final String INPUT_VALUE_2 = "inputValue2";
    private static final String[] PARAMETERS_NAMES = {"param1", "param2"};
    private static final String METHOD_NAME = "methodName";

    private SpelExpressionHelper spelExpressionHelper = new SpelExpressionHelper();

    @Test
    void testParseExpression() {
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {INPUT_VALUE_1, INPUT_VALUE_2});
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getName()).thenReturn(METHOD_NAME);
        when(signature.getParameterNames()).thenReturn(PARAMETERS_NAMES);
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        Object actualValue = spelExpressionHelper.parseExpression(proceedingJoinPoint, "#param1");
        assertThat(actualValue).isNotNull();
        assertThat(String.valueOf(actualValue)).isEqualTo(INPUT_VALUE_1);
    }
}
