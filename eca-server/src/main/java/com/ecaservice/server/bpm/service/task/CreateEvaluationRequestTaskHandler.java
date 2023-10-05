package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.model.evaluation.AbstractClassifierRequestDataModel;
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

    /**
     * Constructor with parameters.
     *
     * @param evaluationRequestService - evaluation request service
     */
    public CreateEvaluationRequestTaskHandler(EvaluationRequestService evaluationRequestService) {
        super(TaskType.CREATE_EVALUATION_WEB_REQUEST);
        this.evaluationRequestService = evaluationRequestService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to create evaluation request [{}] for process", execution.getProcessBusinessKey());
        var evaluationRequest =
                getVariable(execution, EVALUATION_REQUEST_DATA, AbstractClassifierRequestDataModel.class);
        var evaluationLog = evaluationRequestService.createAndSaveEvaluationRequest(evaluationRequest);
        execution.setVariable(EVALUATION_LOG_ID, evaluationLog.getId());
        log.info("Evaluation request [{}] has been created for process", execution.getProcessBusinessKey());
    }
}
