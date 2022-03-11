package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.ExperimentRequestRepository;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Auto test service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationRequestService {

    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;
    private final ExperimentRequestRepository experimentRequestRepository;

    /**
     * Gets experiment request by request id.
     *
     * @param requestId - request id
     * @return experiment request entity
     */
    public ExperimentRequestEntity getByRequestId(String requestId) {
        return experimentRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentRequestEntity.class, requestId));
    }

    /**
     * Finish request entity test with error.
     *
     * @param requestEntity - request entity
     * @param errorMessage  - error message
     */
    public void finishWithError(BaseEvaluationRequestEntity requestEntity, String errorMessage) {
        requestEntity.setStageType(RequestStageType.ERROR);
        requestEntity.setTestResult(TestResult.ERROR);
        requestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        requestEntity.setDetails(errorMessage);
        requestEntity.setFinished(LocalDateTime.now());
        baseEvaluationRequestRepository.save(requestEntity);
        log.debug("Evaluation auto test [{}] has been finished with error", requestEntity.getId());
    }
}
