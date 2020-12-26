package com.ecaservice.external.api.test.bpm.listeners;

import com.ecaservice.external.api.test.bpm.model.ExecutionResult;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.EXECUTION_RESULT;
import static com.ecaservice.external.api.test.util.CamundaUtils.errorResult;

/**
 * Abstract execution listener implementation. Method execution result is an object of type
 * {@link ExecutionResult} in the process variable
 * {@link com.ecaservice.external.api.test.bpm.CamundaVariables#EXECUTION_RESULT}.
 *
 * @author Roman Batygin
 */
@Slf4j
public abstract class AbstractExecutionListener implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.debug("Delegate execution for process key [{}], execution id [{}]", execution.getProcessBusinessKey(),
                execution.getId());
        try {
            ExecutionResult executionResult = proceedResult(execution);
            saveResult(execution, executionResult);
            log.debug("Delegate execution for process key [{}], execution id [{}] result: [{}]",
                    execution.getProcessBusinessKey(), execution.getId(), executionResult);
        } catch (Exception ex) {
            log.error("There was an error in execution [{}]: [{}]", execution.getId(), ex.getMessage(), ex);
            ExecutionResult errorResult = errorResult(ex.getMessage());
            saveResult(execution, errorResult);
        }
    }

    protected abstract ExecutionResult proceedResult(DelegateExecution delegateExecution) throws Exception;

    private void saveResult(DelegateExecution execution, ExecutionResult executionResult) {
        execution.setVariable(EXECUTION_RESULT, executionResult);
    }
}
