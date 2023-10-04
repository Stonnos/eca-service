package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.service.evaluation.EvaluationLogDataService;
import com.ecaservice.server.service.evaluation.EvaluationRequestService;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import com.ecaservice.server.service.experiment.ExperimentService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_LOG_ID;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_STATUS;
import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Evaluation processor task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationProcessorTaskHandler extends AbstractTaskHandler {

    private final EvaluationLogDataService evaluationLogDataService;
    private final EvaluationRequestService evaluationRequestService;

    /**
     * Constructor with parameters.
     *
     * @param evaluationLogDataService - evaluation log data service
     * @param evaluationRequestService     - evaluation request service
     */
    public EvaluationProcessorTaskHandler(EvaluationLogDataService evaluationLogDataService,
                                          EvaluationRequestService evaluationRequestService) {
        super(TaskType.PROCESS_CLASSIFIER_EVALUATION);
        this.evaluationLogDataService = evaluationLogDataService;
        this.evaluationRequestService = evaluationRequestService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to handle classifier evaluation [{}] processing task", execution.getProcessBusinessKey());
        Long id = getVariable(execution, EVALUATION_LOG_ID, Long.class);
        var evaluationLog = evaluationLogDataService.getById(id);
        var evaluationResultsDataModel
                = evaluationRequestService.processEvaluationRequest(evaluationLog);
        execution.setVariable(EVALUATION_REQUEST_STATUS, evaluationResultsDataModel.getStatus().name());
        log.info("Classifier evaluation [{}] processing task has been finished", execution.getProcessBusinessKey());
    }
}
