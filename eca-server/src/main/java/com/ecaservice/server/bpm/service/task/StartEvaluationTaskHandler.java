package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.service.evaluation.EvaluationLogDataService;
import com.ecaservice.server.service.evaluation.EvaluationLogService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_LOG_ID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Starts classifier evaluation task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class StartEvaluationTaskHandler extends AbstractTaskHandler {

    private final EvaluationLogDataService evaluationLogDataService;
    private final EvaluationLogService evaluationLogService;

    /**
     * Constructor with parameters.
     *
     * @param evaluationLogDataService - evaluation log data service
     * @param evaluationLogService     - evaluation log service
     */
    public StartEvaluationTaskHandler(EvaluationLogDataService evaluationLogDataService,
                                      EvaluationLogService evaluationLogService) {
        super(TaskType.START_EVALUATION);
        this.evaluationLogDataService = evaluationLogDataService;
        this.evaluationLogService = evaluationLogService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to process evaluation [{}] start task", execution.getProcessBusinessKey());
        Long id = getVariable(execution, EVALUATION_LOG_ID, Long.class);
        var evaluationLog = evaluationLogDataService.getById(id);
        evaluationLogService.startEvaluation(evaluationLog);
        log.info("Evaluation [{}] start task has been processed", execution.getProcessBusinessKey());
    }
}
