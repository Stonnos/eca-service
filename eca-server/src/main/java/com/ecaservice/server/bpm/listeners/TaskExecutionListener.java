package com.ecaservice.server.bpm.listeners;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.bpm.service.task.AbstractTaskHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.server.bpm.CamundaVariables.TASK_TYPE;
import static com.ecaservice.server.util.CamundaUtils.getEnumFromExecution;

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
        var taskType = getEnumFromExecution(execution, TaskType.class, TASK_TYPE);
        log.debug("Starting to process task [{}] for process key [{}], execution id [{}]", taskType,
                execution.getProcessBusinessKey(), execution.getId());
        var taskHandler = taskHandlers.stream()
                .filter(handler -> handler.getTaskType().equals(taskType))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(String.format("Can't handle task with type [%s]", taskType)));
        taskHandler.handle(execution);
        if ( MDC.get(TX_ID) != null) {
            log.info("MDC TX_ID: {}", MDC.get(TX_ID) );
        }
        log.debug("Task [{}] has been successfully processed for process key [{}], execution id [{}]", taskType,
                execution.getProcessBusinessKey(), execution.getId());
        log.debug("Delegate execution for process key [{}], execution id [{}] has been processed",
                execution.getProcessBusinessKey(), execution.getId());
    }
}
