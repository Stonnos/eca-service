package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.util.Assert;

import java.util.List;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.API_RESPONSE;
import static com.ecaservice.external.api.test.util.CamundaUtils.setVariableSafe;

/**
 * Abstract handler for external api calls.
 *
 * @author Roman Batygin
 */
@Slf4j
public abstract class ExternalApiTaskHandler extends AbstractTaskHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Constructor with parameters.
     *
     * @param taskType - task type
     */
    protected ExternalApiTaskHandler(TaskType taskType) {
        super(taskType);
    }

    @Override
    public void handle(DelegateExecution execution) throws Exception {
        log.debug("External api task execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        try {
            internalHandle(execution);
        } catch (FeignException.BadRequest ex) {
            handleBadRequest(execution, ex);
        }
        log.debug("External api task [{}], process key [{}] has been executed", execution.getId(),
                execution.getProcessBusinessKey());
    }

    protected abstract void internalHandle(DelegateExecution execution) throws Exception;

    private void handleBadRequest(DelegateExecution execution, FeignException.BadRequest ex)
            throws JsonProcessingException {
        log.debug("Got bad request for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        String responseBody = ex.contentUTF8();
        Assert.notNull(responseBody, "Expected not empty response body");
        var responseDto =
                OBJECT_MAPPER.readValue(responseBody, new TypeReference<>() {
                });
        setVariableSafe(execution, API_RESPONSE, responseDto);
    }
}
