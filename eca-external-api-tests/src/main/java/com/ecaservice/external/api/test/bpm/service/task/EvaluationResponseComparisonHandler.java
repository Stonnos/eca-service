package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.MatchResult;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.service.TestResultsMatcher;
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
@Component
public class EvaluationResponseComparisonHandler extends ComparisonTaskHandler {

    private static final String DOWNLOAD_URL_FORMAT = "%s/download-model/%s";

    private final ExternalApiTestsConfig externalApiTestsConfig;

    /**
     * Constructor with parameters.
     *
     * @param autoTestRepository - auto test repository bean
     * @param externalApiTestsConfig - external api config bean
     */
    public EvaluationResponseComparisonHandler(AutoTestRepository autoTestRepository,
                                               ExternalApiTestsConfig externalApiTestsConfig) {
        super(TaskType.COMPARE_EVALUATION_RESPONSE_RESULT, autoTestRepository);
        this.externalApiTestsConfig = externalApiTestsConfig;
    }

    @Override
    protected void compareAndMatchFields(DelegateExecution execution,
                                         AutoTestEntity autoTestEntity,
                                         TestResultsMatcher matcher) {
        TestDataModel testDataModel = getVariable(execution, TEST_DATA_MODEL, TestDataModel.class);
        EvaluationResponseDto evaluationResponseDto =
                getVariable(execution, EVALUATION_RESPONSE, EvaluationResponseDto.class);
        //Compare and match evaluation response status
        compareAndMatchRequestStatus(autoTestEntity, testDataModel.getExpectedResponse().getRequestStatus(),
                null, matcher);
        //Compare and match model url
        String actualModelUrl = getValueSafe(null, EvaluationResponseDto::getModelUrl);
        String expectedModelUrl =
                String.format(DOWNLOAD_URL_FORMAT, externalApiTestsConfig.getDownloadBaseUrl(),
                        evaluationResponseDto.getRequestId());
        autoTestEntity.setExpectedModelUrl(expectedModelUrl);
        autoTestEntity.setActualModelUrl(actualModelUrl);
        MatchResult modelUrlMatchResult = matcher.compareAndMatch(expectedModelUrl, actualModelUrl);
        autoTestEntity.setModelUrlMatchResult(modelUrlMatchResult);
    }
}
