package com.ecaservice.external.api.test.bpm.listeners;

import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.bpm.service.task.AbstractTaskHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.TASK_RESULT;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TASK_TYPE;
import static com.ecaservice.external.api.test.util.CamundaUtils.error;
import static com.ecaservice.external.api.test.util.CamundaUtils.getEnumFromExecution;
import static com.ecaservice.external.api.test.util.CamundaUtils.setVariableSafe;
import static com.ecaservice.external.api.test.util.CamundaUtils.success;

/**
 * Implements service task execution listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskExecutionListener implements JavaDelegate {

    private final List<AbstractTaskHandler> taskHandlers;

    @Override
    public void execute(DelegateExecution execution) {
        log.debug("Starting delegate execution for process key [{}], execution id [{}]",
                execution.getProcessBusinessKey(), execution.getId());
        TaskType taskType = getEnumFromExecution(execution, TaskType.class, TASK_TYPE);
        try {
            log.debug("Starting to process task [{}]", taskType);
            AbstractTaskHandler taskHandler = taskHandlers.stream()
                    .filter(handler -> handler.canHandle(taskType))
                    .findFirst()
                    .orElseThrow(
                            () -> new IllegalStateException(String.format("Can't handle task with type [%s]", taskType)));
            taskHandler.handle(execution);
            setVariableSafe(execution, TASK_RESULT, success());
            log.debug("Task [{}] has been successfully processed", taskType);
        } catch (Exception ex) {
            log.error("Error while handle task [{}]: {}", taskType, ex.getMessage());
            setVariableSafe(execution, TASK_RESULT, error(ex.getMessage()));
        }
        log.debug("Delegate execution for process key [{}], execution id [{}] has been processed",
                execution.getProcessBusinessKey(), execution.getId());
    }
}
