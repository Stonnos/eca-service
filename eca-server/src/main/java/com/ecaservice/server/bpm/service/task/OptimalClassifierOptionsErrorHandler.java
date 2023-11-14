package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.EvaluationResultsModel;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.entity.RequestStatus;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.CLASSIFIER_OPTIONS_RESULT;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_RESULTS_DATA;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Optimal classifier options error handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class OptimalClassifierOptionsErrorHandler extends AbstractTaskHandler {

    /**
     * Constructor with parameters.
     */
    public OptimalClassifierOptionsErrorHandler() {
        super(TaskType.OPTIMAL_CLASSIFIER_OPTIONS_ERROR_HANDLER);
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to handle optimal classifier options error request for evaluation [{}]",
                execution.getProcessBusinessKey());
        var classifierOptionsResult =
                getVariable(execution, CLASSIFIER_OPTIONS_RESULT, ClassifierOptionsResult.class);
        var evaluationResultsModel = new EvaluationResultsModel();
        evaluationResultsModel.setRequestId(execution.getProcessBusinessKey());
        evaluationResultsModel.setRequestStatus(RequestStatus.ERROR);
        evaluationResultsModel.setErrorCode(classifierOptionsResult.getErrorCode());
        execution.setVariable(EVALUATION_RESULTS_DATA, evaluationResultsModel);
        log.info("Optimal classifier options error request has been processed for evaluation [{}]",
                execution.getProcessBusinessKey());
    }
}
