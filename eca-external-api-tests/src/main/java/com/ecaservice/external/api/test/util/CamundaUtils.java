package com.ecaservice.external.api.test.util;

import com.ecaservice.external.api.test.exception.ProcessVariableNotFound;
import lombok.experimental.UtilityClass;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.text.MessageFormat;

/**
 * Camunda utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class CamundaUtils {

    /**
     * Gets variable from execution.
     *
     * @param execution    - execution context
     * @param variableName - variable name
     * @param clazz        - value class type
     * @param <T>          - variable value type
     * @return variable value
     */
    public static <T> T getVariable(DelegateExecution execution, String variableName, Class<T> clazz) {
        Object value = execution.getVariable(variableName);
        if (value == null) {
            throw new ProcessVariableNotFound(variableName, execution.getProcessInstanceId());
        }
        Assert.isInstanceOf(clazz, value,
                MessageFormat.format("Expected [{0}] type for variable [{1}], but found [{2}]", clazz.getSimpleName(),
                        variableName, value.getClass().getSimpleName()));
        return clazz.cast(value);
    }

    /**
     * Gets variable from execution.
     *
     * @param execution     - execution context
     * @param variableName  - variable name
     * @param typeReference - type reference
     * @param <T>           - variable value type
     * @return variable value
     */
    @SuppressWarnings("unchecked")
    public static <T> T getVariable(DelegateExecution execution, String variableName,
                                    ParameterizedTypeReference<T> typeReference) {
        Object value = execution.getVariable(variableName);
        if (value == null) {
            throw new ProcessVariableNotFound(variableName, execution.getProcessInstanceId());
        }
        ResolvableType resolvableType = ResolvableType.forType(typeReference);
        return (T) resolvableType.toClass().cast(value);
    }

    /**
     * Safely set variable value to execution. If value is null then exception will be thrown.
     *
     * @param execution    - execution context
     * @param variableName - variable name
     * @param value        - variable value
     */
    public static void setVariableSafe(DelegateExecution execution, String variableName, Object value) {
        Assert.notNull(value, String.format("Expected not null variable [%s] to set", variableName));
        execution.setVariable(variableName, value);
    }

    /**
     * Gets enum value from execution.
     *
     * @param execution    - execution context
     * @param enumType     - enum class type
     * @param variableName - variable name
     * @param <T>          - variable value type
     * @return enum value
     */
    public static <T extends Enum<T>> T getEnumFromExecution(DelegateExecution execution, Class<T> enumType,
                                                             String variableName) {
        Object enumValue = execution.getVariable(variableName);
        if (enumValue == null) {
            throw new ProcessVariableNotFound(variableName, execution.getProcessInstanceId());
        }
        return Enum.valueOf(enumType, String.valueOf(enumValue));
    }
}
