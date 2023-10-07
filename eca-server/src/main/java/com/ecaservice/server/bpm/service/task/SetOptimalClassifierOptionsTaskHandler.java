package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.EvaluationRequestModel;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.model.ClassifierOptionsResult;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.CLASSIFIER_OPTIONS_RESULT;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;
import static com.ecaservice.server.util.CamundaUtils.getVariable;
import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;

/**
 * Get optimal classifier options task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class SetOptimalClassifierOptionsTaskHandler extends AbstractTaskHandler {

    /**
     * Constructor with parameters.
     */
    public SetOptimalClassifierOptionsTaskHandler() {
        super(TaskType.SET_OPTIMAL_CLASSIFIER_OPTIONS);
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to set optimal classifier options task for evaluation request [{}]",
                execution.getProcessBusinessKey());
        var evaluationRequestModel =
                getVariable(execution, EVALUATION_REQUEST_DATA, EvaluationRequestModel.class);
        var classifierOptionsResult =
                getVariable(execution, CLASSIFIER_OPTIONS_RESULT, ClassifierOptionsResult.class);
        evaluationRequestModel.setClassifierOptions(parseOptions(classifierOptionsResult.getOptionsJson()));
        execution.setVariable(EVALUATION_REQUEST_DATA, evaluationRequestModel);
        log.info("Optimal classifier options task has been set for evaluation request [{}]",
                execution.getProcessBusinessKey());
    }
}
