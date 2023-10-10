package com.ecaservice.server.bpm.listeners;

import com.ecaservice.server.exception.BpmnProcessErrorException;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
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
public class BpmErrorHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.debug("Handles error for execution [{}], process id [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        String errorCode =  getVariable(execution, ERROR_CODE, String.class);
        String errorMessage = getVariable(execution, ERROR_MESSAGE, String.class);
        log.error("Got error [{}] code for process key [{}]. Error details: {}", errorCode,
                execution.getProcessBusinessKey(), errorMessage);
        throw new BpmnProcessErrorException(errorCode, errorMessage);
    }
}
