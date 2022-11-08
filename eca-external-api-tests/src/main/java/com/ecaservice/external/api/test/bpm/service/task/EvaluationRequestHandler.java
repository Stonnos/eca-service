package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.model.EvaluationTestDataModel;
import com.ecaservice.external.api.test.service.api.ExternalApiClient;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.API_RESPONSE;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;
import static com.ecaservice.external.api.test.util.CamundaUtils.setVariableSafe;

/**
 * Implements handler to send evaluation request.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationRequestHandler extends ExternalApiTaskHandler {

    private final ExternalApiClient externalApiClient;

    /**
     * Constructor with spring dependency injection.
     *
     * @param externalApiClient - external api client bean
     */
    public EvaluationRequestHandler(ExternalApiClient externalApiClient) {
        super(TaskType.EVALUATION_REQUEST);
        this.externalApiClient = externalApiClient;
    }

    @Override
    protected void internalHandle(DelegateExecution execution) {
        log.debug("Evaluation request execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        var testDataModel = getVariable(execution, TEST_DATA_MODEL, EvaluationTestDataModel.class);
        log.debug("Starting to send evaluation request for test [{}]", autoTestId);
        var response = externalApiClient.evaluateRequest(testDataModel.getRequest());
        log.debug("Received evaluation response for test [{}]: {}", autoTestId, response);
        setVariableSafe(execution, API_RESPONSE, response);
        log.debug("Evaluation request execution [{}], process key [{}] has been finished", execution.getId(),
                execution.getProcessBusinessKey());
    }
}
