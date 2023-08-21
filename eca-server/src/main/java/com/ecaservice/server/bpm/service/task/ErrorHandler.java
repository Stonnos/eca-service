package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.ERROR_CODE;
import static com.ecaservice.server.bpm.CamundaVariables.ERROR_MESSAGE;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Error handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ErrorHandler extends AbstractTaskHandler {

    public ErrorHandler() {
        super(TaskType.ERROR);
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.debug("Handles error for execution [{}], process id [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        String errorCode =  getVariable(execution, ERROR_CODE, String.class);
        String errorMessage = getVariable(execution, ERROR_MESSAGE, String.class);
        log.error("Got error [{}] code for process key [{}]. Error details: {}", errorCode,
                execution.getProcessBusinessKey(), errorMessage);
        throw new BpmnError(errorCode, errorMessage);
    }
}
