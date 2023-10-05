package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.dto.CreateEvaluationRequestDto;
import com.ecaservice.server.model.evaluation.EvaluationWebRequestDataModel;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.evaluation.EvaluationRequestService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_LOG_ID;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;
import static com.ecaservice.server.bpm.CamundaVariables.TRAIN_DATA_UUID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Creates classifier evaluation web request task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class CreateEvaluationWebRequestTaskHandler extends AbstractTaskHandler {

    private final EvaluationRequestService evaluationRequestService;
    private final UserService userService;

    /**
     * Constructor with parameters.
     *
     * @param evaluationRequestService - evaluation request service
     * @param userService              - user service
     */
    public CreateEvaluationWebRequestTaskHandler(EvaluationRequestService evaluationRequestService,
                                                 UserService userService) {
        super(TaskType.CREATE_EVALUATION_WEB_REQUEST);
        this.evaluationRequestService = evaluationRequestService;
        this.userService = userService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to create evaluation request [{}] for process", execution.getProcessBusinessKey());
        var evaluationRequest = createEvaluationRequest(execution);
        var evaluationLog = evaluationRequestService.createAndSaveEvaluationRequest(evaluationRequest);
        execution.setVariable(EVALUATION_LOG_ID, evaluationLog.getId());
        log.info("Evaluation request [{}] has been created for process", execution.getProcessBusinessKey());
    }

    private EvaluationWebRequestDataModel createEvaluationRequest(DelegateExecution execution) {
        var evaluationRequestDto =
                getVariable(execution, EVALUATION_REQUEST_DATA, CreateEvaluationRequestDto.class);
        String trainDataUuid = getVariable(execution, TRAIN_DATA_UUID, String.class);
        var evaluationWebRequestDataModel = new EvaluationWebRequestDataModel();
        evaluationWebRequestDataModel.setRequestId(execution.getProcessBusinessKey());
        evaluationWebRequestDataModel.setDataUuid(trainDataUuid);
        evaluationWebRequestDataModel.setCreatedBy(userService.getCurrentUser());
        evaluationWebRequestDataModel.setEvaluationMethod(evaluationRequestDto.getEvaluationMethod());
        evaluationWebRequestDataModel.setNumFolds(evaluationRequestDto.getNumFolds());
        evaluationWebRequestDataModel.setNumTests(evaluationRequestDto.getNumTests());
        evaluationWebRequestDataModel.setSeed(evaluationRequestDto.getSeed());
        return evaluationWebRequestDataModel;
    }
}
