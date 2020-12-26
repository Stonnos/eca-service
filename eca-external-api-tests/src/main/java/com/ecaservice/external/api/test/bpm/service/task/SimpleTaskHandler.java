package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.test.bpm.model.ExecutionResult;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import static com.ecaservice.external.api.test.util.CamundaUtils.successResult;

/**
 * Simple task handler.
 *
 * @author Roman Batygin
 */
public abstract class SimpleTaskHandler extends AbstractTaskHandler {

    /**
     * Constructor with parameters.
     *
     * @param taskType - task type
     */
    protected SimpleTaskHandler(TaskType taskType) {
        super(taskType);
    }

    @Override
    public ExecutionResult handle(DelegateExecution execution) throws Exception {
        internalHandle(execution);
        return successResult();
    }

    protected abstract void internalHandle(DelegateExecution execution) throws Exception;
}
