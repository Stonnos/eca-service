package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.test.bpm.model.ExecutionResult;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.service.AutoTestService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.EXECUTION_RESULT;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

/**
 * Error handler.
 *
 * @author Roman Batygin
 */
@Component
public class ErrorHandler extends SimpleTaskHandler {

    private final AutoTestService autoTestService;

    /**
     * Constructor with parameters.
     *
     * @param autoTestService - auto test service
     */
    public ErrorHandler(AutoTestService autoTestService) {
        super(TaskType.FINISH_WITH_ERROR);
        this.autoTestService = autoTestService;
    }

    @Override
    public void internalHandle(DelegateExecution execution) {
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        ExecutionResult lastErrorExecutionResult = getVariable(execution, EXECUTION_RESULT, ExecutionResult.class);
        autoTestService.finishWithError(autoTestId, lastErrorExecutionResult.getErrorMessage());
    }
}
