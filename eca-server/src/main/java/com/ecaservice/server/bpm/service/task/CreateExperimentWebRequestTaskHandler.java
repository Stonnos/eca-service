package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.dto.CreateExperimentRequestDto;
import com.ecaservice.server.model.experiment.ExperimentWebRequestData;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.experiment.ExperimentService;
import com.ecaservice.user.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_REQUEST_DATA;
import static com.ecaservice.server.bpm.CamundaVariables.TRAIN_DATA_UUID;
import static com.ecaservice.server.bpm.CamundaVariables.USER_INFO;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Create experiment web request task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class CreateExperimentWebRequestTaskHandler extends AbstractTaskHandler {

    private final ExperimentService experimentService;
    private final UserService userService;

    /**
     * Constructor with parameters.
     *
     * @param experimentService - experiment service
     * @param userService       - user service
     */
    public CreateExperimentWebRequestTaskHandler(ExperimentService experimentService, UserService userService) {
        super(TaskType.CREATE_EXPERIMENT_WEB_REQUEST);
        this.experimentService = experimentService;
        this.userService = userService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to create experiment [{}] for process", execution.getProcessBusinessKey());
        var experimentMessageRequestData = createExperimentRequest(execution);
        experimentService.createExperiment(experimentMessageRequestData);
        log.info("Experiment [{}] has been created for process", execution.getProcessBusinessKey());
    }

    private ExperimentWebRequestData createExperimentRequest(DelegateExecution execution) {
        var createExperimentRequestDto =
                getVariable(execution, EXPERIMENT_REQUEST_DATA, CreateExperimentRequestDto.class);
        var userInfoDto = getVariable(execution, USER_INFO, UserInfoDto.class);
        String trainDataUuid = getVariable(execution, TRAIN_DATA_UUID, String.class);
        var experimentWebRequestData = new ExperimentWebRequestData();
        experimentWebRequestData.setRequestId(execution.getProcessBusinessKey());
        experimentWebRequestData.setDataUuid(trainDataUuid);
        experimentWebRequestData.setEmail(userInfoDto.getEmail());
        experimentWebRequestData.setCreatedBy(userService.getCurrentUser());
        experimentWebRequestData.setExperimentType(createExperimentRequestDto.getExperimentType());
        experimentWebRequestData.setEvaluationMethod(createExperimentRequestDto.getEvaluationMethod());
        return experimentWebRequestData;
    }
}
