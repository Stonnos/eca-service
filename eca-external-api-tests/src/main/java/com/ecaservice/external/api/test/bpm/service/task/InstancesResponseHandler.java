package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.model.AbstractTestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TRAIN_DATA_UUID;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;
import static com.ecaservice.external.api.test.util.CamundaUtils.setVariableSafe;

/**
 * Handler for processing instances response.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class InstancesResponseHandler extends AbstractTaskHandler {

    private final ObjectMapper objectMapper;
    private final AutoTestRepository autoTestRepository;

    /**
     * Constructor with parameters.
     *
     * @param objectMapper       - object mapper bean
     * @param autoTestRepository - auto test repository bean.
     */
    public InstancesResponseHandler(ObjectMapper objectMapper,
                                    AutoTestRepository autoTestRepository) {
        super(TaskType.PROCESS_INSTANCES_RESPONSE);
        this.objectMapper = objectMapper;
        this.autoTestRepository = autoTestRepository;
    }

    @Override
    public void handle(DelegateExecution execution) throws JsonProcessingException {
        log.debug("Handles instances response for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        var testDataModel = getVariable(execution, TEST_DATA_MODEL, AbstractTestDataModel.class);
        var trainDataUuid = getVariable(execution, TRAIN_DATA_UUID, String.class);
        AutoTestEntity autoTestEntity = autoTestRepository.findById(autoTestId)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestEntity.class, autoTestId));
        log.debug("Update train data uuid [{}] for execution [{}], process key [{}]", trainDataUuid, execution.getId(),
                execution.getProcessBusinessKey());
        testDataModel.getRequest().setTrainDataUuid(trainDataUuid);
        autoTestEntity.setRequest(objectMapper.writeValueAsString(testDataModel.getRequest()));
        autoTestRepository.save(autoTestEntity);
        setVariableSafe(execution, TEST_DATA_MODEL, testDataModel);
        log.debug("Instances response for execution [{}], process key [{}] has been processed", execution.getId(),
                execution.getProcessBusinessKey());
    }
}
