package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.service.UserProfileOptionsService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.USER_LOGIN;
import static com.ecaservice.server.bpm.CamundaVariables.USER_PROFILE_OPTIONS;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Gets user profile options task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class GetUserProfileOptionsTaskHandler extends AbstractTaskHandler {

    private final UserProfileOptionsService userProfileOptionsService;

    /**
     * Constructor with parameters.
     *
     * @param userProfileOptionsService - user profile service
     */
    public GetUserProfileOptionsTaskHandler(UserProfileOptionsService userProfileOptionsService) {
        super(TaskType.GET_USER_PROFILE_OPTIONS);
        this.userProfileOptionsService = userProfileOptionsService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to get user profile options [{}] for process", execution.getProcessBusinessKey());
        String userName = getVariable(execution, USER_LOGIN, String.class);
        var userProfileOptionsModel = userProfileOptionsService.getUserProfileOptionsModel(userName);
        execution.setVariable(USER_PROFILE_OPTIONS, userProfileOptionsModel);
        log.info("User profile options has been fetched for process [{}]", execution.getProcessBusinessKey());
    }
}
