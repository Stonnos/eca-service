package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.ResponseCode;
import com.ecaservice.external.api.test.model.AbstractTestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.test.common.model.MatchResult;
import com.ecaservice.test.common.service.TestResultsMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.RESPONSE_CODE;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.util.CamundaUtils.getEnumFromExecution;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

/**
 * API response comparison task handler.
 *
 * @param <RESP> - response generic type
 * @author Roman Batygin
 */
@Slf4j
public class ApiResponseComparisonTaskHandler<RESP> extends ComparisonTaskHandler {

    private final ObjectMapper objectMapper;
    private final AutoTestRepository autoTestRepository;

    /**
     * Constructor with parameters.
     *
     * @param taskType           - task type
     * @param objectMapper       - object mapper bean
     * @param autoTestRepository - auto test repository
     */
    public ApiResponseComparisonTaskHandler(TaskType taskType,
                                            ObjectMapper objectMapper,
                                            AutoTestRepository autoTestRepository) {
        super(taskType);
        this.objectMapper = objectMapper;
        this.autoTestRepository = autoTestRepository;
    }

    @Override
    protected void compareAndMatchFields(DelegateExecution execution,
                                         Long autoTestId,
                                         TestResultsMatcher matcher) throws Exception {
        var testDataModel = getVariable(execution, TEST_DATA_MODEL, AbstractTestDataModel.class);
        ResponseCode actualResponseCode = getEnumFromExecution(execution, ResponseCode.class, RESPONSE_CODE);
        var autoTestEntity = autoTestRepository.findById(autoTestId)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestEntity.class, autoTestId));
        autoTestEntity.setResponse(objectMapper.writeValueAsString(actualResponseCode));
        compareAndMatchResponseCode(autoTestEntity, testDataModel.getExpectedResponseCode(), actualResponseCode,
                matcher);
        autoTestRepository.save(autoTestEntity);
    }

    private void compareAndMatchResponseCode(AutoTestEntity autoTestEntity,
                                             ResponseCode expectedResponseCode,
                                             ResponseCode actualResponseCode,
                                             TestResultsMatcher matcher) {
        log.debug("Compare status field for auto test [{}]", autoTestEntity.getId());
        autoTestEntity.setExpectedResponseCode(expectedResponseCode);
        autoTestEntity.setActualResponseCode(actualResponseCode);
        MatchResult statusMatchResult = matcher.compareAndMatch(expectedResponseCode, actualResponseCode);
        autoTestEntity.setResponseCodeMatchResult(statusMatchResult);
        log.debug("Auto test [{}] expected response code [{}], actual response code [{}], match result [{}]",
                autoTestEntity.getId(), expectedResponseCode, actualResponseCode, statusMatchResult);
    }
}
