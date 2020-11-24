package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.test.entity.ExecutionStatus;
import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.repository.JobRepository;
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

    private final JobRepository jobRepository;

    /**
     * Creates new auto tests job.
     *
     * @return auto tests job
     */
    public JobEntity createAndSaveNewJob() {
        JobEntity jobEntity = new JobEntity();
        jobEntity.setJobUuid(UUID.randomUUID().toString());
        jobEntity.setNumThreads(1);
        jobEntity.setExecutionStatus(ExecutionStatus.NEW);
        jobEntity.setCreated(LocalDateTime.now());
        return jobRepository.save(jobEntity);
    }
}
