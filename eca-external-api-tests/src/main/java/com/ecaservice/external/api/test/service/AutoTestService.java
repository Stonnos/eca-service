package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.ExecutionStatus;
import com.ecaservice.external.api.test.entity.TestResult;
import com.ecaservice.external.api.test.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Auto test service.
 *
 * @author Roman Batygin
 */
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
        autoTestRepository.save(autoTestEntity);
    }

    /**
     * Calculates final test result.
     *
     * @param id      - auto test id
     * @param matcher - matcher object
     */
    public void calculateFinalTestResult(Long id, TestResultsMatcher matcher) {
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
        autoTestRepository.save(autoTestEntity);
    }
}
