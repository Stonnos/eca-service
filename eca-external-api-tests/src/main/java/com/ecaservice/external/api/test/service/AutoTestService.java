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
    }
}
