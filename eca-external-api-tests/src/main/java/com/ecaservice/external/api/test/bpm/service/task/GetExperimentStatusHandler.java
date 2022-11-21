package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.service.AutoTestService;
import com.ecaservice.external.api.test.service.api.ExternalApiClient;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.API_RESPONSE;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;
import static com.ecaservice.external.api.test.util.CamundaUtils.setVariableSafe;

/**
 * Implements handler to get experiment response status.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class GetExperimentStatusHandler extends ExternalApiTaskHandler {

    private final ExternalApiClient externalApiClient;
    private final AutoTestService autoTestService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param externalApiClient - external api client bean
     * @param autoTestService   - auto test service bean
     */
    public GetExperimentStatusHandler(ExternalApiClient externalApiClient,
                                      AutoTestService autoTestService) {
        super(TaskType.GET_EXPERIMENT_STATUS);
        this.externalApiClient = externalApiClient;
        this.autoTestService = autoTestService;
    }

    @Override
    protected void internalHandle(DelegateExecution execution) {
        log.debug("Get experiment response status for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        var autoTestEntity = autoTestService.getById(autoTestId);
        log.debug("Starting to get experiment response status for test [{}]", autoTestId);
        var response = externalApiClient.getExperimentResults(autoTestEntity.getRequestId());
        log.debug("Received experiment response status for test [{}]: {}", autoTestId, response);
        setVariableSafe(execution, API_RESPONSE, response);
        log.debug("Get experiment response status for execution [{}], process key [{}] has been finished",
                execution.getId(), execution.getProcessBusinessKey());
    }
}
