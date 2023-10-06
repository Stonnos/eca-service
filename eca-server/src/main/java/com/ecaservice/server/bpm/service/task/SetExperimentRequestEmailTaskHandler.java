package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.ExperimentRequestModel;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.user.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;
import static com.ecaservice.server.bpm.CamundaVariables.USER_INFO;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Set experiment request email task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class SetExperimentRequestEmailTaskHandler extends AbstractTaskHandler {

    /**
     * Constructor with parameters.
     */
    public SetExperimentRequestEmailTaskHandler() {
        super(TaskType.SET_EXPERIMENT_REQUEST_EMAIL);
    }

    @Override
    public void handle(DelegateExecution execution) {
        var experimentRequestModel =
                getVariable(execution, EVALUATION_REQUEST_DATA, ExperimentRequestModel.class);
        var userInfoDto = getVariable(execution, USER_INFO, UserInfoDto.class);
        experimentRequestModel.setEmail(userInfoDto.getEmail());
        execution.setVariable(EVALUATION_REQUEST_DATA, experimentRequestModel);
    }
}
