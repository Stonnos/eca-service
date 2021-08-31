package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.API_RESPONSE;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;
import static com.ecaservice.external.api.test.util.Utils.getValueSafe;

/**
 * Handler for processing evaluation request response.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationRequestResponseHandler extends AbstractTaskHandler {

    private static final ParameterizedTypeReference<ResponseDto<EvaluationResponseDto>> API_RESPONSE_TYPE_REFERENCE =
            new ParameterizedTypeReference<ResponseDto<EvaluationResponseDto>>() {
            };

    private final AutoTestRepository autoTestRepository;

    /**
     * Constructor with parameters.
     *
     * @param autoTestRepository - auto test repository bean.
     */
    public EvaluationRequestResponseHandler(AutoTestRepository autoTestRepository) {
        super(TaskType.PROCESS_EVALUATION_REQUEST_RESPONSE);
        this.autoTestRepository = autoTestRepository;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.debug("Handles instances response for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        var responseDto = getVariable(execution, API_RESPONSE, API_RESPONSE_TYPE_REFERENCE);
        Assert.notNull(responseDto.getPayload(),
                String.format("Expected not null evaluation response for auto test [%d]", autoTestId));
        Assert.notNull(responseDto.getPayload().getRequestId(),
                String.format("Expected not null evaluation request ID for auto test [%d]", autoTestId));
        saveRequestId(autoTestId, responseDto);
        log.debug("Instances response for execution [{}], process key [{}] has been processed", execution.getId(),
                execution.getProcessBusinessKey());
    }

    private void saveRequestId(long autoTestId, ResponseDto<EvaluationResponseDto> responseDto) {
        AutoTestEntity autoTestEntity = autoTestRepository.findById(autoTestId)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestEntity.class, autoTestId));
        String requestId = getValueSafe(responseDto, EvaluationResponseDto::getRequestId);
        autoTestEntity.setRequestId(requestId);
        autoTestRepository.save(autoTestEntity);
    }
}
