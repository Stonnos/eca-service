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
     * @param autoTestType - auto test type
     * @return auto tests job entity
     */
    public AutoTestsJobEntity createAutoTestsJob(AutoTestType autoTestType) {
        var autoTestsJobEntity = new AutoTestsJobEntity();
        autoTestsJobEntity.setJobUuid(UUID.randomUUID().toString());
        autoTestsJobEntity.setAutoTestType(autoTestType);
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
     * Success completes auto test job.
     *
     * @param autoTestsJobEntity - auto tests job
     */
    public void finish(AutoTestsJobEntity autoTestsJobEntity) {
        autoTestsJobEntity.setExecutionStatus(ExecutionStatus.FINISHED);
        autoTestsJobEntity.setFinished(LocalDateTime.now());
        autoTestsJobRepository.save(autoTestsJobEntity);
        log.info("Auto tests job [{}] has been finished", autoTestsJobEntity.getJobUuid());
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
