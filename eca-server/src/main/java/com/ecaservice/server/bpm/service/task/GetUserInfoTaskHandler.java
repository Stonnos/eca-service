package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.auth.UsersClient;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.USER_INFO;

/**
 * Gets user info task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class GetUserInfoTaskHandler extends AbstractTaskHandler {

    private final UsersClient usersClient;
    private final UserService userService;

    /**
     * Constructor with parameters.
     *
     * @param usersClient - users client
     * @param userService - user service
     */
    public GetUserInfoTaskHandler(UsersClient usersClient,
                                  UserService userService) {
        super(TaskType.GET_USER_INFO);
        this.usersClient = usersClient;
        this.userService = userService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to get user info [{}] for process", execution.getProcessBusinessKey());
        var user = userService.getCurrentUser();
        var userInfoDto = usersClient.getUserInfo(user);
        execution.setVariable(USER_INFO, userInfoDto);
        log.info("User info has been fetched for process [{}]", execution.getProcessBusinessKey());
    }
}
