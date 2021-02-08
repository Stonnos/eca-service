package com.ecaservice.external.api.test.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.ExecutionStatus;
import com.ecaservice.external.api.test.entity.TestResult;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
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
public class AutoTestService {

    private final AutoTestRepository autoTestRepository;

    /**
     * Gets auto test entity by id.
     *
     * @param id - auto test id
     * @return auto test entity
     */
    public AutoTestEntity getById(Long id) {
        return autoTestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestEntity.class, id));
    }

    /**
     * Finish auto test with error.
     *
     * @param id           - auto test id
     * @param errorMessage - error message
     */
    public void finishWithError(Long id, String errorMessage) {
        AutoTestEntity autoTestEntity = getById(id);
        autoTestEntity.setTestResult(TestResult.ERROR);
        autoTestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        autoTestEntity.setDetails(errorMessage);
        autoTestEntity.setFinished(LocalDateTime.now());
        autoTestRepository.save(autoTestEntity);
        log.debug("Auto test [{}] has been finished with error", autoTestEntity.getId());
    }

    /**
     * Calculates final test result.
     *
     * @param id      - auto test id
     * @param matcher - matcher object
     */
    public void calculateFinalTestResult(Long id, TestResultsMatcher matcher) {
        log.debug("Calculates final test result for auto test [{}]", id);
        AutoTestEntity autoTestEntity = getById(id);
        autoTestEntity.setTotalMatched(matcher.getTotalMatched());
        autoTestEntity.setTotalNotMatched(matcher.getTotalNotMatched());
        autoTestEntity.setTotalNotFound(matcher.getTotalNotFound());
        if (matcher.getTotalNotMatched() == 0 && matcher.getTotalNotFound() == 0) {
            autoTestEntity.setTestResult(TestResult.PASSED);
        } else {
            autoTestEntity.setTestResult(TestResult.FAILED);
        }
        autoTestEntity.setExecutionStatus(ExecutionStatus.FINISHED);
        autoTestEntity.setFinished(LocalDateTime.now());
        autoTestRepository.save(autoTestEntity);
    }
}
