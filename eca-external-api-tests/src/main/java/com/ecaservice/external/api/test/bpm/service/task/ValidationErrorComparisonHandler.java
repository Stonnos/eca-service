package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.dto.ValidationErrorDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.service.TestResultsMatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class ValidationErrorComparisonHandler extends ComparisonTaskHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Constructor with parameters.
     *
     * @param autoTestRepository - auto test repository bean
     */
    public ValidationErrorComparisonHandler(AutoTestRepository autoTestRepository) {
        super(TaskType.COMPARE_VALIDATION_ERROR_RESULT, autoTestRepository);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void compareAndMatchFields(DelegateExecution execution,
                                         AutoTestEntity autoTestEntity,
                                         TestResultsMatcher matcher) throws JsonProcessingException {
        log.debug("Compare validation error status for execution id [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        TestDataModel testDataModel = getVariable(execution, TEST_DATA_MODEL, TestDataModel.class);
        ResponseDto<List<ValidationErrorDto>> responseDto =
                getVariable(execution, VALIDATION_ERROR_RESPONSE, ResponseDto.class);
        autoTestEntity.setResponse(OBJECT_MAPPER.writeValueAsString(responseDto));
        compareAndMatchRequestStatus(autoTestEntity, testDataModel.getExpectedResponse().getRequestStatus(),
                responseDto.getRequestStatus(), matcher);
        log.debug("Comparison validation error status has been finished for execution id [{}], process key [{}]",
                execution.getId(), execution.getProcessBusinessKey());
    }
}
