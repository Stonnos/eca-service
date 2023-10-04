package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.service.evaluation.EvaluationLogDataService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_LOG;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_LOG_ID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Gets evaluation log details task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class GetEvaluationLogDetailsTaskHandler extends AbstractTaskHandler {

    private final EvaluationLogDataService evaluationLogDataService;

    /**
     * Constructor with parameters.
     *
     * @param evaluationLogDataService - evaluation log data service
     */
    public GetEvaluationLogDetailsTaskHandler(EvaluationLogDataService evaluationLogDataService) {
        super(TaskType.GET_EVALUATION_DETAILS);
        this.evaluationLogDataService = evaluationLogDataService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        Long id = getVariable(execution, EVALUATION_LOG_ID, Long.class);
        log.info("Starting to get evaluation [{}] details for process", execution.getProcessBusinessKey());
        var evaluationLogModel = evaluationLogDataService.getEvaluationLogModel(id);
        execution.setVariable(EVALUATION_LOG, evaluationLogModel);
        log.info("Evaluation [{}] details has been fetched for process", execution.getProcessBusinessKey());
    }
}
