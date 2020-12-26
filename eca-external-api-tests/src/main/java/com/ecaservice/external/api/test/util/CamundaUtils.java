package com.ecaservice.external.api.test.util;

import com.ecaservice.external.api.test.bpm.model.ExecutionResult;
import com.ecaservice.external.api.test.bpm.model.TaskExecutionStatus;
import com.ecaservice.external.api.test.exception.ProcessVariableNotFound;
import lombok.experimental.UtilityClass;
import org.camunda.bpm.engine.delegate.DelegateExecution;
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

    /**
     * Creates execution result with success status.
     *
     * @return success result
     */
    public static ExecutionResult successResult() {
        return ExecutionResult.builder()
                .status(TaskExecutionStatus.SUCCESS)
                .build();
    }

    /**
     * Creates execution result with error status.
     *
     * @param errorMessage - error message
     * @return error result
     */
    public static ExecutionResult errorResult(String errorMessage) {
        return ExecutionResult.builder()
                .status(TaskExecutionStatus.ERROR)
                .errorMessage(errorMessage)
                .build();
    }
}
