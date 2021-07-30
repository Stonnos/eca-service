package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.ExperimentRequestStageType;
import com.ecaservice.auto.test.repository.ExperimentRequestRepository;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import eca.converters.model.ExperimentHistory;
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
public class ExperimentRequestService {

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
     * Finish experiment auto test with error.
     *
     * @param experimentRequestEntity - experiment auto entity
     * @param errorMessage            - error message
     */
    public void finishWithError(ExperimentRequestEntity experimentRequestEntity, String errorMessage) {
        experimentRequestEntity.setStageType(ExperimentRequestStageType.ERROR);
        experimentRequestEntity.setTestResult(TestResult.ERROR);
        experimentRequestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        experimentRequestEntity.setDetails(errorMessage);
        experimentRequestEntity.setFinished(LocalDateTime.now());
        experimentRequestRepository.save(experimentRequestEntity);
        log.debug("Experiment auto test [{}] has been finished with error", experimentRequestEntity.getId());
    }

    /**
     * Compare and match experiment history results.
     *
     * @param experimentRequestEntity - experiment history entity
     * @param experimentHistory       - experiment history results
     */
    public void compareAndMatchResults(ExperimentRequestEntity experimentRequestEntity,
                                       ExperimentHistory experimentHistory) {
        log.info("Starting to compare and match experiment [{}] results", experimentRequestEntity.getRequestId());

    }
}
