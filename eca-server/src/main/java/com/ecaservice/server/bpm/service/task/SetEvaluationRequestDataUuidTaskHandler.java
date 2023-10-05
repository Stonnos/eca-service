package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.model.AbstractEvaluationRequestData;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;
import static com.ecaservice.server.bpm.CamundaVariables.TRAIN_DATA_UUID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Set evaluation request data uuid task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class SetEvaluationRequestDataUuidTaskHandler extends AbstractTaskHandler {

    /**
     * Constructor with parameters.
     */
    public SetEvaluationRequestDataUuidTaskHandler() {
        super(TaskType.SET_EVALUATION_REQUEST_DATA_UUID);
    }

    @Override
    public void handle(DelegateExecution execution) {
        var evaluationRequestData =
                getVariable(execution, EVALUATION_REQUEST_DATA, AbstractEvaluationRequestData.class);
        String trainDataUuid = getVariable(execution, TRAIN_DATA_UUID, String.class);
        evaluationRequestData.setDataUuid(trainDataUuid);
        execution.setVariable(EVALUATION_REQUEST_DATA, evaluationRequestData);
    }
}
