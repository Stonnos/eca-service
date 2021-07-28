package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.AutoTestsJobEntity;
import com.ecaservice.auto.test.repository.AutoTestsJobRepository;
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
        autoTestsJobEntity.setExecutionStatus(ExecutionStatus.NEW);
        autoTestsJobEntity.setCreated(LocalDateTime.now());
        return autoTestsJobRepository.save(autoTestsJobEntity);
    }
}
