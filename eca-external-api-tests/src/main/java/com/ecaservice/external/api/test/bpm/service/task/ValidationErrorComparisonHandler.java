package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.test.common.service.TestResultsMatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.API_RESPONSE;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

/**
 * Implements handler to compare validation error result.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ValidationErrorComparisonHandler extends ComparisonTaskHandler {

    private static final ParameterizedTypeReference<ResponseDto<List<ValidationErrorDto>>> API_RESPONSE_TYPE_REFERENCE =
            new ParameterizedTypeReference<ResponseDto<List<ValidationErrorDto>>>() {
            };
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
    protected void compareAndMatchFields(DelegateExecution execution,
                                         AutoTestEntity autoTestEntity,
                                         TestResultsMatcher matcher) throws JsonProcessingException {
        log.debug("Compare validation error status for execution id [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        TestDataModel testDataModel = getVariable(execution, TEST_DATA_MODEL, TestDataModel.class);
        var responseDto = getVariable(execution, API_RESPONSE, API_RESPONSE_TYPE_REFERENCE);
        autoTestEntity.setResponse(OBJECT_MAPPER.writeValueAsString(responseDto));
        compareAndMatchResponseCode(autoTestEntity, testDataModel.getExpectedResponse().getResponseCode(),
                responseDto.getResponseCode(), matcher);
        log.debug("Comparison validation error status has been finished for execution id [{}], process key [{}]",
                execution.getId(), execution.getProcessBusinessKey());
    }
}
