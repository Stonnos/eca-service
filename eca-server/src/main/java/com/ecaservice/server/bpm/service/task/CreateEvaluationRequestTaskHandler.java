package com.ecaservice.server.bpm.service.task;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.server.bpm.model.EvaluationRequestModel;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.service.evaluation.EvaluationRequestService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_LOG_ID;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Creates classifier evaluation web request task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class CreateEvaluationRequestTaskHandler extends AbstractTaskHandler {

    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationLogMapper evaluationLogMapper;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;

    /**
     * Constructor with parameters.
     *
     * @param evaluationRequestService - evaluation request service
     * @param evaluationLogMapper      - evaluation log mapper
     * @param classifierOptionsAdapter - classifier options adapter
     */
    public CreateEvaluationRequestTaskHandler(EvaluationRequestService evaluationRequestService,
                                              EvaluationLogMapper evaluationLogMapper,
                                              ClassifierOptionsAdapter classifierOptionsAdapter) {
        super(TaskType.CREATE_EVALUATION_REQUEST);
        this.evaluationRequestService = evaluationRequestService;
        this.evaluationLogMapper = evaluationLogMapper;
        this.classifierOptionsAdapter = classifierOptionsAdapter;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to create evaluation request [{}] for process", execution.getProcessBusinessKey());
        var evaluationRequestModel =
                getVariable(execution, EVALUATION_REQUEST_DATA, EvaluationRequestModel.class);
        var evaluationRequestData = evaluationLogMapper.map(evaluationRequestModel);
        evaluationRequestData.setClassifier(
                classifierOptionsAdapter.convert(evaluationRequestModel.getClassifierOptions()));
        var evaluationLog = evaluationRequestService.createAndSaveEvaluationRequest(evaluationRequestData);
        execution.setVariable(EVALUATION_LOG_ID, evaluationLog.getId());
        log.info("Evaluation request [{}] has been created for process", execution.getProcessBusinessKey());
    }
}
