package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.repository.JobRepository;
import com.ecaservice.test.common.model.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Auto tests job service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final ExternalApiTestsConfig externalApiTestsConfig;
    private final JobRepository jobRepository;

    /**
     * Creates new auto tests job.
     *
     * @return auto tests job
     */
    public JobEntity createAndSaveNewJob() {
        JobEntity jobEntity = new JobEntity();
        jobEntity.setJobUuid(UUID.randomUUID().toString());
        jobEntity.setNumThreads(externalApiTestsConfig.getNumThreads());
        jobEntity.setExecutionStatus(ExecutionStatus.NEW);
        jobEntity.setCreated(LocalDateTime.now());
        return jobRepository.save(jobEntity);
    }
}
