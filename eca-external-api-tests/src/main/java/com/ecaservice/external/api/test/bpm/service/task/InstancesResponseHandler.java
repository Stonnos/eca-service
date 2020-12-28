package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.INSTANCES_RESPONSE;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_DATA_MODEL;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

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
    @SuppressWarnings("unchecked")
    public void handle(DelegateExecution execution) throws JsonProcessingException {
        log.debug("Handles instances response for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        TestDataModel testDataModel = getVariable(execution, TEST_DATA_MODEL, TestDataModel.class);
        ResponseDto<InstancesDto> responseDto = getVariable(execution, INSTANCES_RESPONSE, ResponseDto.class);
        Assert.notNull(responseDto.getPayload(),
                String.format("Expected not null instances response for auto test [%d]", autoTestId));
        AutoTestEntity autoTestEntity = autoTestRepository.findById(autoTestId)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestEntity.class, autoTestId));
        log.debug("Update train data url [{}] for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey(), responseDto.getPayload().getDataUrl());
        testDataModel.getRequest().setTrainDataUrl(responseDto.getPayload().getDataUrl());
        autoTestEntity.setRequest(objectMapper.writeValueAsString(testDataModel.getRequest()));
        autoTestRepository.save(autoTestEntity);
        execution.setVariable(TEST_DATA_MODEL, testDataModel);
        log.debug("Instances response for execution [{}], process key [{}] has been processed", execution.getId(),
                execution.getProcessBusinessKey());
    }
}
