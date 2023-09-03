package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.model.AbstractTestDataModel;
import com.ecaservice.test.common.service.DataLoaderService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TRAIN_DATA_UUID;
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

    private final DataLoaderService dataLoaderService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param dataLoaderService - data loader service
     */
    public UploadTrainDataHandler(DataLoaderService dataLoaderService) {
        super(TaskType.UPLOAD_TRAINING_DATA);
        this.dataLoaderService = dataLoaderService;
    }

    @Override
    protected void internalHandle(DelegateExecution execution) {
        log.debug("Handles upload train data for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        var testDataModel = getVariable(execution, TEST_DATA_MODEL, AbstractTestDataModel.class);
        log.debug("Starting to uploads train data [{}] to server for test [{}]",
                testDataModel.getTrainDataPath(), autoTestId);
        Resource resource = resolver.getResource(testDataModel.getTrainDataPath());
        var dataUuid = dataLoaderService.uploadInstances(resource);
        log.debug("Train data has been uploaded with uuid [{}] for test [{}]",
                dataUuid, autoTestId);
        setVariableSafe(execution, TRAIN_DATA_UUID, dataUuid);
        log.debug("Train data uploading has been finished for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
    }
}
