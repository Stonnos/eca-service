package com.ecaservice.external.api.test.bpm.listeners;

import com.ecaservice.external.api.test.bpm.model.ExecutionResult;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.bpm.service.task.AbstractTaskHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
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
public class TaskExecutionListener extends AbstractExecutionListener {

    private final List<AbstractTaskHandler> taskHandlers;

    @Override
    protected ExecutionResult proceedResult(DelegateExecution execution) throws Exception {
        TaskType taskType = getEnumFromExecution(execution, TaskType.class, TASK_TYPE);
        AbstractTaskHandler taskHandler = taskHandlers.stream()
                .filter(handler -> handler.canHandle(taskType))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(String.format("Can't handle task with type [%s]", taskType)));
        return taskHandler.handle(execution);
    }
}
