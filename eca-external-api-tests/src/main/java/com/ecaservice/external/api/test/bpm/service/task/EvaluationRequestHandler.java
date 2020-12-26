package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.exception.ProcessEvaluationException;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.service.api.ExternalApiClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.EVALUATION_RESPONSE;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;
import static com.ecaservice.external.api.test.util.Utils.getValueSafe;

/**
 * Implements handler to send evaluation request.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationRequestHandler extends ExternalApiTaskHandler {

    private final ExternalApiClient externalApiClient;
    private final ObjectMapper objectMapper;
    private final AutoTestRepository autoTestRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param externalApiClient  - external api client bean
     * @param objectMapper       - object mapper bean
     * @param autoTestRepository - auto test repository bean
     */
    public EvaluationRequestHandler(ExternalApiClient externalApiClient,
                                    ObjectMapper objectMapper,
                                    AutoTestRepository autoTestRepository) {
        super(TaskType.EVALUATION_REQUEST);
        this.externalApiClient = externalApiClient;
        this.objectMapper = objectMapper;
        this.autoTestRepository = autoTestRepository;
    }

    @Override
    protected void internalHandle(DelegateExecution execution) throws JsonProcessingException {
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
        saveResponse(autoTestId, response);
        execution.setVariable(EVALUATION_RESPONSE, response);
    }

    private void saveResponse(Long autoTestId, ResponseDto<EvaluationResponseDto> response)
            throws JsonProcessingException {
        AutoTestEntity autoTestEntity = autoTestRepository.findById(autoTestId)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestEntity.class, autoTestId));
        autoTestEntity.setResponse(objectMapper.writeValueAsString(response));
        String requestId = getValueSafe(response, EvaluationResponseDto::getRequestId);
        autoTestEntity.setRequestId(requestId);
        autoTestRepository.save(autoTestEntity);
    }
}
