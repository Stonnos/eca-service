package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.test.bpm.model.ExecutionResult;
import com.ecaservice.external.api.test.bpm.model.TaskExecutionStatus;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import feign.FeignException;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.util.Assert;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.VALIDATION_ERROR_RESPONSE;

/**
 * Abstract handler for external api calls.
 *
 * @author Roman Batygin
 */
public abstract class ExternalApiTaskHandler extends AbstractTaskHandler {

    /**
     * Constructor with parameters.
     *
     * @param taskType - task type
     */
    protected ExternalApiTaskHandler(TaskType taskType) {
        super(taskType);
    }

    @Override
    public ExecutionResult handle(DelegateExecution execution) throws Exception {
        ExecutionResult executionResult = new ExecutionResult();
        try {
            internalHandle(execution);
            executionResult.setStatus(TaskExecutionStatus.SUCCESS);
        } catch (FeignException.BadRequest ex) {
            handleBadRequest(execution, ex, executionResult);
        }
        return executionResult;
    }

    protected abstract void internalHandle(DelegateExecution execution) throws Exception;

    private void handleBadRequest(DelegateExecution execution,
                                  FeignException.BadRequest ex,
                                  ExecutionResult executionResult) {
        String responseBody = ex.contentUTF8();
        Assert.notNull(responseBody, "Expected not empty response body");
        executionResult.setStatus(TaskExecutionStatus.INVALID_DATA);
        execution.setVariable(VALIDATION_ERROR_RESPONSE, responseBody);
    }
}
