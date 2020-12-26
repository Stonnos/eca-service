package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.exception.UploadTrainDataException;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.service.ExternalApiService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.INSTANCES_PAYLOAD;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

/**
 * Implements handler to uploads train data to server.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UploadTrainDataHandler extends ExternalApiTaskHandler {

    private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

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
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        TestDataModel testDataModel = getVariable(execution, TEST_DATA_MODEL, TestDataModel.class);
        log.debug("Starting to uploads train data [{}] to server for test [{}]",
                testDataModel.getTrainDataPath(), autoTestId);
        Resource resource = resolver.getResource(testDataModel.getTrainDataPath());
        ResponseDto<InstancesDto> instancesDto = externalApiService.uploadInstances(resource);
        log.debug("Train data has been uploaded with status [{}] for test [{}]",
                instancesDto.getRequestStatus(), autoTestId);
        if (!RequestStatus.SUCCESS.equals(instancesDto.getRequestStatus())) {
            throw new UploadTrainDataException(autoTestId);
        }
        Assert.notNull(instancesDto.getPayload(),
                String.format("Expected not null instances response for auto test [%d]", autoTestId));
        execution.setVariable(INSTANCES_PAYLOAD, instancesDto.getPayload());
    }
}
