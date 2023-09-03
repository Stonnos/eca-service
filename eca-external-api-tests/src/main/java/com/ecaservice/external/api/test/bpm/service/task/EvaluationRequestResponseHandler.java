package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.dto.SimpleEvaluationResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.API_RESPONSE;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

/**
 * Handler for processing evaluation request response.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationRequestResponseHandler extends AbstractTaskHandler {

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
        log.debug("Handles evaluation request response for execution [{}], process key [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        var evaluationResponseDto
                = getVariable(execution, API_RESPONSE, SimpleEvaluationResponseDto.class);
        saveRequestId(autoTestId, evaluationResponseDto);
        log.debug("Instances evaluation request for execution [{}], process key [{}] has been processed",
                execution.getId(), execution.getProcessBusinessKey());
    }

    private void saveRequestId(long autoTestId, SimpleEvaluationResponseDto responseDto) {
        AutoTestEntity autoTestEntity = autoTestRepository.findById(autoTestId)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestEntity.class, autoTestId));
        autoTestEntity.setRequestId(responseDto.getRequestId());
        autoTestRepository.save(autoTestEntity);
    }
}
