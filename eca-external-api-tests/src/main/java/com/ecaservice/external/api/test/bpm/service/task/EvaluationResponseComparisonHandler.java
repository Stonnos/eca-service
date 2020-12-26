package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.MatchResult;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.service.TestResultsMatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.EVALUATION_RESPONSE;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;
import static com.ecaservice.external.api.test.util.Utils.getValueSafe;

/**
 * Implements handler to compare validation error result.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationResponseComparisonHandler extends ComparisonTaskHandler {

    private static final String DOWNLOAD_URL_FORMAT = "%s/download-model/%s";

    private final ExternalApiTestsConfig externalApiTestsConfig;
    private final ObjectMapper objectMapper;

    /**
     * Constructor with parameters.
     *
     * @param autoTestRepository     - auto test repository bean
     * @param externalApiTestsConfig - external api config bean
     * @param objectMapper           - object mapper bean
     */
    public EvaluationResponseComparisonHandler(AutoTestRepository autoTestRepository,
                                               ExternalApiTestsConfig externalApiTestsConfig,
                                               ObjectMapper objectMapper) {
        super(TaskType.COMPARE_EVALUATION_RESPONSE_RESULT, autoTestRepository);
        this.externalApiTestsConfig = externalApiTestsConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void compareAndMatchFields(DelegateExecution execution,
                                         AutoTestEntity autoTestEntity,
                                         TestResultsMatcher matcher) throws JsonProcessingException {
        log.debug("Compare evaluation response for execution id [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        TestDataModel testDataModel = getVariable(execution, TEST_DATA_MODEL, TestDataModel.class);
        ResponseDto<EvaluationResponseDto> responseDto = getVariable(execution, EVALUATION_RESPONSE, ResponseDto.class);
        saveResponse(autoTestEntity, responseDto);
        //Compare and match evaluation response status
        compareAndMatchRequestStatus(autoTestEntity, testDataModel.getExpectedResponse().getRequestStatus(),
                responseDto.getRequestStatus(), matcher);
        //Compare and match model url
        compareAndMatchModelUrl(responseDto, autoTestEntity, matcher);
        log.debug("Compare evaluation response has been finished for execution id [{}], process key [{}]", execution.getId(), execution.getProcessBusinessKey());
    }

    private void compareAndMatchModelUrl(ResponseDto<EvaluationResponseDto> responseDto,
                                         AutoTestEntity autoTestEntity,
                                         TestResultsMatcher matcher) {
        log.debug("Compare model url field for auto test [{}]", autoTestEntity.getId());
        String actualModelUrl = getValueSafe(responseDto, EvaluationResponseDto::getModelUrl);
        String expectedModelUrl =
                String.format(DOWNLOAD_URL_FORMAT, externalApiTestsConfig.getDownloadBaseUrl(),
                        responseDto.getPayload().getRequestId());
        autoTestEntity.setExpectedModelUrl(expectedModelUrl);
        autoTestEntity.setActualModelUrl(actualModelUrl);
        MatchResult modelUrlMatchResult = matcher.compareAndMatch(expectedModelUrl, actualModelUrl);
        autoTestEntity.setModelUrlMatchResult(modelUrlMatchResult);
        log.debug("Auto test [{}], expected model url [{}], actual model url [{}], match result [{}]",
                autoTestEntity.getId(), expectedModelUrl, actualModelUrl, modelUrlMatchResult);
    }

    private void saveResponse(AutoTestEntity autoTestEntity, ResponseDto<EvaluationResponseDto> response)
            throws JsonProcessingException {
        autoTestEntity.setResponse(objectMapper.writeValueAsString(response));
        String requestId = getValueSafe(response, EvaluationResponseDto::getRequestId);
        autoTestEntity.setRequestId(requestId);
    }
}
