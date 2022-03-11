package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.repository.autotest.AutoTestsJobRepository;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.test.common.model.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Auto test service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTestJobService {

    private final AutoTestsJobRepository autoTestsJobRepository;

    /**
     * Creates experiment auto tests job.
     *
     * @return auto tests job entity
     */
    public AutoTestsJobEntity createExperimentsAutoTestsJob() {
        var autoTestsJobEntity = new AutoTestsJobEntity();
        autoTestsJobEntity.setJobUuid(UUID.randomUUID().toString());
        autoTestsJobEntity.setAutoTestType(AutoTestType.EXPERIMENT_REQUEST_PROCESS);
        autoTestsJobEntity.setExecutionStatus(ExecutionStatus.NEW);
        autoTestsJobEntity.setCreated(LocalDateTime.now());
        return autoTestsJobRepository.save(autoTestsJobEntity);
    }

    /**
     * Finds auto tests job by uuid.
     *
     * @param jobUuid - job uuid
     * @return auto tests job entity
     */
    public AutoTestsJobEntity getJob(String jobUuid) {
        return autoTestsJobRepository.findByJobUuid(jobUuid)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestsJobEntity.class, jobUuid));
    }

    /**
     * Finishes auto tests job with error
     *
     * @param autoTestsJobEntity - auto tests job entity
     * @param errorMessage       - error message
     */
    public void finishWithError(AutoTestsJobEntity autoTestsJobEntity, String errorMessage) {
        autoTestsJobEntity.setDetails(errorMessage);
        autoTestsJobEntity.setExecutionStatus(ExecutionStatus.ERROR);
        autoTestsJobEntity.setFinished(LocalDateTime.now());
        autoTestsJobRepository.save(autoTestsJobEntity);
    }
}
