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
 * Implements handler to get evaluation response status.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class GetEvaluationStatusHandler extends ExternalApiTaskHandler {

    private final ExternalApiClient externalApiClient;
    private final AutoTestService autoTestService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param externalApiClient - external api client bean
     * @param autoTestService   - auto test service bean
     */
    public GetEvaluationStatusHandler(ExternalApiClient externalApiClient,
                                      AutoTestService autoTestService) {
        super(TaskType.GET_EVALUATION_STATUS);
        this.externalApiClient = externalApiClient;
        this.autoTestService = autoTestService;
    }

    @Override
    protected void internalHandle(DelegateExecution execution) {
        log.debug("Get evaluation response status for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        var autoTestEntity = autoTestService.getById(autoTestId);
        log.debug("Starting to get evaluation response status for test [{}]", autoTestId);
        var evaluationResultsResponseDto
                = externalApiClient.getEvaluationResults(autoTestEntity.getRequestId());
        log.debug("Received evaluation response status for test [{}]: {}", autoTestId, evaluationResultsResponseDto);
        setVariableSafe(execution, API_RESPONSE, evaluationResultsResponseDto);
        log.debug("Get evaluation response status for execution [{}], process key [{}] has been finished",
                execution.getId(), execution.getProcessBusinessKey());
    }
}
