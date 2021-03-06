package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.service.AutoTestService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.ERROR_MESSAGE;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

/**
 * Error handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ErrorHandler extends AbstractTaskHandler {

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
    public void handle(DelegateExecution execution) {
        log.debug("Handles error for execution [{}], process id [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        String errorMessage = getVariable(execution, ERROR_MESSAGE, String.class);
        autoTestService.finishWithError(autoTestId, errorMessage);
    }
}
