package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.exception.ProcessEvaluationException;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.service.api.ExternalApiClient;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.EVALUATION_RESPONSE;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

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
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        TestDataModel testDataModel = getVariable(execution, TEST_DATA_MODEL, TestDataModel.class);
        log.debug("Starting to send evaluation request for test [{}]", autoTestId);
        ResponseDto<EvaluationResponseDto> response = externalApiClient.evaluateModel(testDataModel.getRequest());
        log.debug("Received evaluation response for test [{}]: {}", autoTestId, response);
        if (!RequestStatus.SUCCESS.equals(response.getRequestStatus())) {
            throw new ProcessEvaluationException(autoTestId);
        }
        Assert.notNull(response.getPayload(),
                String.format("Expected not null evaluation response for auto test [%d]", autoTestId));
        execution.setVariable(EVALUATION_RESPONSE, response.getPayload());
    }
}
