package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
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
public class EvaluationResponseComparisonHandler extends ComparisonTaskHandler {

    private static final ParameterizedTypeReference<ResponseDto<EvaluationResponseDto>> API_RESPONSE_TYPE_REFERENCE =
            new ParameterizedTypeReference<ResponseDto<EvaluationResponseDto>>() {
            };

    private final ObjectMapper objectMapper;

    /**
     * Constructor with parameters.
     *
     * @param autoTestRepository - auto test repository bean
     * @param objectMapper       - object mapper bean
     */
    public EvaluationResponseComparisonHandler(AutoTestRepository autoTestRepository,
                                               ObjectMapper objectMapper) {
        super(TaskType.COMPARE_EVALUATION_RESPONSE_RESULT, autoTestRepository);
        this.objectMapper = objectMapper;
    }

    @Override
    protected void compareAndMatchFields(DelegateExecution execution,
                                         AutoTestEntity autoTestEntity,
                                         TestResultsMatcher matcher) throws JsonProcessingException {
        log.debug("Compare evaluation response for execution id [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        TestDataModel testDataModel = getVariable(execution, TEST_DATA_MODEL, TestDataModel.class);
        var responseDto = getVariable(execution, API_RESPONSE, API_RESPONSE_TYPE_REFERENCE);
        saveResponse(autoTestEntity, responseDto);
        //Compare and match evaluation response status
        compareAndMatchResponseCode(autoTestEntity, testDataModel.getExpectedResponse().getResponseCode(),
                responseDto.getResponseCode(), matcher);
        log.debug("Compare evaluation response has been finished for execution id [{}], process key [{}]",
                execution.getId(), execution.getProcessBusinessKey());
    }

    private void saveResponse(AutoTestEntity autoTestEntity,
                              ResponseDto<EvaluationResponseDto> response) throws JsonProcessingException {
        autoTestEntity.setResponse(objectMapper.writeValueAsString(response));
    }
}
