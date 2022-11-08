package com.ecaservice.external.api.test.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.dto.AbstractAutoTestDto;
import com.ecaservice.external.api.test.dto.AutoTestType;
import com.ecaservice.external.api.test.dto.AutoTestsJobDto;
import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.mapping.AutoTestMapper;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.repository.JobRepository;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.report.TestResultsCounter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final AutoTestMapper autoTestMapper;
    private final AutoTestRequestAdapter autoTestRequestAdapter;
    private final AutoTestRepository autoTestRepository;
    private final JobRepository jobRepository;

    /**
     * Creates new auto tests job.
     *
     * @param autoTestType - auto test type
     * @return auto tests job
     */
    public AutoTestsJobDto createAndSaveNewJob(AutoTestType autoTestType) {
        var jobEntity = new JobEntity();
        jobEntity.setJobUuid(UUID.randomUUID().toString());
        jobEntity.setAutoTestType(autoTestType);
        jobEntity.setNumThreads(externalApiTestsConfig.getNumThreads());
        jobEntity.setExecutionStatus(ExecutionStatus.NEW);
        jobEntity.setCreated(LocalDateTime.now());
        jobRepository.save(jobEntity);
        return autoTestMapper.map(jobEntity);
    }

    /**
     * Finishes auto test job with error.
     *
     * @param jobEntity    - job entity
     * @param errorMessage - error message
     */
    public void finishWithError(JobEntity jobEntity, String errorMessage) {
        log.info("Starting to finish auto test job [{}] with error", jobEntity.getJobUuid());
        jobEntity.setDetails(errorMessage);
        jobEntity.setExecutionStatus(ExecutionStatus.ERROR);
        jobEntity.setFinished(LocalDateTime.now());
        jobRepository.save(jobEntity);
        log.info("Auto test job [{}] has been finished with error", jobEntity.getJobUuid());
    }

    /**
     * Gets auto tests job details.
     *
     * @param jobUuid - job uuid
     * @return auto tests job dto
     */
    public AutoTestsJobDto getDetails(String jobUuid) {
        log.info("Gets auto tests job [{}] details", jobUuid);
        var jobEntity = jobRepository.findByJobUuid(jobUuid)
                .orElseThrow(() -> new EntityNotFoundException(JobEntity.class, jobUuid));
        var jobDto = autoTestMapper.map(jobEntity);
        TestResultsCounter counter = new TestResultsCounter();
        var testResults = getTestResults(jobEntity, counter);
        jobDto.setTestResults(testResults);
        jobDto.setSuccess(counter.getPassed());
        jobDto.setFailed(counter.getFailed());
        jobDto.setErrors(counter.getErrors());
        return jobDto;
    }

    private List<AbstractAutoTestDto> getTestResults(JobEntity jobEntity, TestResultsCounter counter) {
        return autoTestRepository.findAllByJob(jobEntity)
                .stream()
                .map(autoTestEntity -> {
                    var autoTestsDto = autoTestRequestAdapter.proceed(autoTestEntity);
                    autoTestEntity.getTestResult().apply(counter);
                    return autoTestsDto;
                })
                .collect(Collectors.toList());
    }
}
