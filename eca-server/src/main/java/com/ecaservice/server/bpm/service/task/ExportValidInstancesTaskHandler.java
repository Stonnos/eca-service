package com.ecaservice.server.bpm.service.task;

import com.ecaservice.data.storage.dto.ExportInstancesResponseDto;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.exception.DataStorageBadRequestException;
import com.ecaservice.server.exception.InstancesValidationException;
import com.ecaservice.server.mapping.DataStorageErrorCodeMapper;
import com.ecaservice.server.model.experiment.ExperimentWebRequestData;
import com.ecaservice.server.service.ds.DataStorageService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;
import static com.ecaservice.server.bpm.CamundaVariables.TRAIN_DATA_UUID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Export valid instances task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExportValidInstancesTaskHandler extends AbstractTaskHandler {

    private final DataStorageService dataStorageService;
    private final DataStorageErrorCodeMapper dataStorageErrorCodeMapper;

    /**
     * Constructor with parameters.
     *
     * @param dataStorageService         - data storage service
     * @param dataStorageErrorCodeMapper - data storage error code mapper
     */
    public ExportValidInstancesTaskHandler(DataStorageService dataStorageService,
                                           DataStorageErrorCodeMapper dataStorageErrorCodeMapper) {
        super(TaskType.EXPORT_VALID_INSTANCES);
        this.dataStorageService = dataStorageService;
        this.dataStorageErrorCodeMapper = dataStorageErrorCodeMapper;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to export valid instances for process [{}]", execution.getProcessBusinessKey());
        var evaluationRequestData =
                getVariable(execution, EVALUATION_REQUEST_DATA, ExperimentWebRequestData.class);
        var exportInstancesResponseDto =
                exportInstances(evaluationRequestData.getInstancesUuid());
        execution.setVariable(TRAIN_DATA_UUID, exportInstancesResponseDto.getExternalDataUuid());
        log.info("Valid instances has been exported for process [{}]", execution.getProcessBusinessKey());
    }

    private ExportInstancesResponseDto exportInstances(String uuid) {
        try {
            return dataStorageService.exportValidInstances(uuid);
        } catch (DataStorageBadRequestException ex) {
            var dsErrorCode = dataStorageErrorCodeMapper.mapErrorCode(ex.getDsErrorCode());
            throw new InstancesValidationException(dsErrorCode, ex.getMessage());
        }
    }
}
