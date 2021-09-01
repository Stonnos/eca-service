package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.service.ExternalApiService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.API_RESPONSE;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;
import static com.ecaservice.external.api.test.util.CamundaUtils.setVariableSafe;

/**
 * Implements handler to uploads train data to server.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UploadTrainDataHandler extends ExternalApiTaskHandler {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    private final ExternalApiService externalApiService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param externalApiService - external api service bean
     */
    public UploadTrainDataHandler(ExternalApiService externalApiService) {
        super(TaskType.UPLOAD_TRAINING_DATA);
        this.externalApiService = externalApiService;
    }

    @Override
    protected void internalHandle(DelegateExecution execution) throws IOException {
        log.debug("Handles upload train data for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        TestDataModel testDataModel = getVariable(execution, TEST_DATA_MODEL, TestDataModel.class);
        log.debug("Starting to uploads train data [{}] to server for test [{}]",
                testDataModel.getTrainDataPath(), autoTestId);
        Resource resource = resolver.getResource(testDataModel.getTrainDataPath());
        var instancesDto = externalApiService.uploadInstances(resource);
        log.debug("Train data has been uploaded with status [{}] for test [{}]",
                instancesDto.getResponseCode(), autoTestId);
        setVariableSafe(execution, API_RESPONSE, instancesDto);
        log.debug("Train data uploading has been finished for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
    }
}
