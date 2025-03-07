package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.EvaluationRequestModel;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.service.evaluation.OptimalClassifierOptionsFetcher;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.CLASSIFIER_OPTIONS_RESULT;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Get optimal classifier options task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class GetOptimalClassifierOptionsTaskHandler extends AbstractTaskHandler {

    private final OptimalClassifierOptionsFetcher optimalClassifierOptionsFetcher;
    private final EvaluationLogMapper evaluationLogMapper;

    /**
     * Constructor with parameters.
     *
     * @param optimalClassifierOptionsFetcher - optimal classifier options fetcher
     * @param evaluationLogMapper             - evaluation log mapper
     */
    public GetOptimalClassifierOptionsTaskHandler(OptimalClassifierOptionsFetcher optimalClassifierOptionsFetcher,
                                                  EvaluationLogMapper evaluationLogMapper) {
        super(TaskType.GET_OPTIMAL_CLASSIFIER_OPTIONS);
        this.optimalClassifierOptionsFetcher = optimalClassifierOptionsFetcher;
        this.evaluationLogMapper = evaluationLogMapper;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to get optimal classifier options task for evaluation request [{}]",
                execution.getProcessBusinessKey());
        var evaluationRequestModel =
                getVariable(execution, EVALUATION_REQUEST_DATA, EvaluationRequestModel.class);
        var instancesRequestDataModel =
                evaluationLogMapper.mapToInstancesRequest(evaluationRequestModel);
        var classifierOptionsResult =
                optimalClassifierOptionsFetcher.getOptimalClassifierOptions(instancesRequestDataModel);
        execution.setVariable(CLASSIFIER_OPTIONS_RESULT, classifierOptionsResult);
        log.info("Optimal classifier options task has been processed for evaluation request [{}]",
                execution.getProcessBusinessKey());
    }
}
