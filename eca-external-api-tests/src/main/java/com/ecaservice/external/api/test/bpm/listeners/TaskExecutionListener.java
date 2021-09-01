package com.ecaservice.external.api.test.bpm.listeners;

import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.bpm.service.task.AbstractTaskHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.TASK_TYPE;
import static com.ecaservice.external.api.test.util.CamundaUtils.getEnumFromExecution;

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
    public void execute(DelegateExecution execution) throws Exception {
        log.debug("Starting delegate execution for process key [{}], execution id [{}]",
                execution.getProcessBusinessKey(), execution.getId());
        TaskType taskType = getEnumFromExecution(execution, TaskType.class, TASK_TYPE);
        log.debug("Starting to process task [{}]", taskType);
        AbstractTaskHandler taskHandler = taskHandlers.stream()
                .filter(handler -> handler.canHandle(taskType))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(String.format("Can't handle task with type [%s]", taskType)));
        taskHandler.handle(execution);
        log.debug("Task [{}] has been processed", taskType);
        log.debug("Delegate execution for process key [{}], execution id [{}] has been processed",
                execution.getProcessBusinessKey(), execution.getId());
    }
}
