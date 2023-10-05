package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.service.evaluation.EvaluationLogDataService;
import com.ecaservice.server.service.evaluation.EvaluationRequestService;
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
    private final EvaluationRequestService evaluationRequestService;

    /**
     * Constructor with parameters.
     *
     * @param evaluationLogDataService - evaluation log data service
     * @param evaluationRequestService - evaluation request service
     */
    public StartEvaluationTaskHandler(EvaluationLogDataService evaluationLogDataService,
                                      EvaluationRequestService evaluationRequestService) {
        super(TaskType.START_EVALUATION);
        this.evaluationLogDataService = evaluationLogDataService;
        this.evaluationRequestService = evaluationRequestService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to process evaluation [{}] start task", execution.getProcessBusinessKey());
        Long id = getVariable(execution, EVALUATION_LOG_ID, Long.class);
        var evaluationLog = evaluationLogDataService.getById(id);
        evaluationRequestService.startEvaluationRequest(evaluationLog);
        log.info("Evaluation [{}] start task has been processed", execution.getProcessBusinessKey());
    }
}
