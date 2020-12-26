package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.dto.ValidationErrorDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.service.TestResultsMatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.VALIDATION_ERROR_RESPONSE;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

/**
 * Implements handler to compare validation error result.
 *
 * @author Roman Batygin
 */
@Component
public class ValidationErrorComparisonHandler extends ComparisonTaskHandler {

    private final ObjectMapper objectMapper;

    /**
     * Constructor with parameters.
     *
     * @param autoTestRepository - auto test repository bean
     * @param objectMapper       - object mapper bean
     */
    public ValidationErrorComparisonHandler(AutoTestRepository autoTestRepository,
                                            ObjectMapper objectMapper) {
        super(TaskType.COMPARE_VALIDATION_ERROR_RESULT, autoTestRepository);
        this.objectMapper = objectMapper;
    }

    @Override
    protected void compareAndMatchFields(DelegateExecution execution,
                                         AutoTestEntity autoTestEntity,
                                         TestResultsMatcher matcher) throws JsonProcessingException {
        TestDataModel testDataModel = getVariable(execution, TEST_DATA_MODEL, TestDataModel.class);
        String responseBody = getVariable(execution, VALIDATION_ERROR_RESPONSE, String.class);
        autoTestEntity.setResponse(responseBody);
        ResponseDto<List<ValidationErrorDto>> responseDto = objectMapper.readValue(responseBody, new TypeReference<>() {
        });
        compareAndMatchRequestStatus(autoTestEntity, testDataModel.getExpectedResponse().getRequestStatus(),
                responseDto.getRequestStatus(), matcher);
    }
}
