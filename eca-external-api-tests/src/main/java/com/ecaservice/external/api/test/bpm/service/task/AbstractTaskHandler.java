package com.ecaservice.external.api.test.bpm.service.task;


import com.ecaservice.external.api.test.bpm.model.ExecutionResult;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.camunda.bpm.engine.delegate.DelegateExecution;

/**
 * Abstract class for task handling.
 *
 * @author Roman Batygin
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTaskHandler {

    /**
     * Task type to handle
     */
    private final TaskType taskType;

    /**
     * Checks if class can handle given task type.
     *
     * @param type - task type
     * @return {@link true} if class can handle task
     */
    public boolean canHandle(final TaskType type) {
        return this.taskType.equals(type);
    }

    /**
     * Handles task with specified params.
     *
     * @param execution - execution context
     * @return execution result
     * @throws Exception in case of error
     */
    public abstract ExecutionResult handle(DelegateExecution execution) throws Exception;
}
