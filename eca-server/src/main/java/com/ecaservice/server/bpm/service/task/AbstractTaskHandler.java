package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
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
     * Handles task with specified params.
     *
     * @param execution - execution context
     */
    public abstract void handle(DelegateExecution execution);
}
