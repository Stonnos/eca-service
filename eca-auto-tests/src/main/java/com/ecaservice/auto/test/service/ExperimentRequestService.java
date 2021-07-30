package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.ExperimentRequestStageType;
import com.ecaservice.auto.test.repository.ExperimentRequestRepository;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.MatchResult;
import com.ecaservice.test.common.model.TestResult;
import com.ecaservice.test.common.service.TestResultsMatcher;
import eca.converters.model.ExperimentHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Auto test service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentRequestService {

    private final AutoTestsProperties autoTestsProperties;
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
    public void processExperimentHistory(ExperimentRequestEntity experimentRequestEntity,
                                         ExperimentHistory experimentHistory) {
        log.info("Starting to compare and match experiment [{}] results", experimentRequestEntity.getRequestId());
        TestResultsMatcher matcher = new TestResultsMatcher();
        compareAndMatchResults(experimentRequestEntity, experimentHistory, matcher);
        experimentRequestEntity.setTotalMatched(matcher.getTotalMatched());
        experimentRequestEntity.setTotalNotMatched(matcher.getTotalNotMatched());
        experimentRequestEntity.setTotalNotFound(matcher.getTotalNotFound());
        if (matcher.getTotalNotMatched() == 0 && matcher.getTotalNotFound() == 0) {
            experimentRequestEntity.setTestResult(TestResult.PASSED);
        } else {
            experimentRequestEntity.setTestResult(TestResult.FAILED);
        }
        experimentRequestEntity.setExecutionStatus(ExecutionStatus.FINISHED);
        experimentRequestEntity.setStageType(ExperimentRequestStageType.COMPLETED);
        experimentRequestEntity.setFinished(LocalDateTime.now());
        experimentRequestRepository.save(experimentRequestEntity);
        log.info("Got experiment request [{}] test result: {}",
                experimentRequestEntity.getRequestId(), experimentRequestEntity.getTestResult());
        log.info("Experiment [{}] results has been processed", experimentRequestEntity.getRequestId());
    }

    private void compareAndMatchResults(ExperimentRequestEntity experimentRequestEntity,
                                        ExperimentHistory experimentHistory,
                                        TestResultsMatcher matcher) {
        int expectedResultsSize = autoTestsProperties.getExpectedResultsSize();
        experimentRequestEntity.setExpectedResultsSize(expectedResultsSize);
        int actualResultsSize = Optional.ofNullable(experimentHistory.getExperiment())
                .map(List::size)
                .orElse(0);
        experimentRequestEntity.setActualResultsSize(actualResultsSize);
        MatchResult statusMatchResult = matcher.compareAndMatch(expectedResultsSize, actualResultsSize);
        experimentRequestEntity.setResultsSizeMatchResult(statusMatchResult);
    }
}
