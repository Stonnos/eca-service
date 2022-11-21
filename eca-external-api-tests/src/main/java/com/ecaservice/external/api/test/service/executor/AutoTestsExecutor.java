package com.ecaservice.external.api.test.service.executor;

import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.repository.JobRepository;
import com.ecaservice.external.api.test.service.runner.AbstractAutoTestRunner;
import com.ecaservice.test.common.model.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Auto tests executor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTestsExecutor {

    private final List<AbstractAutoTestRunner> autoTestRunners;
    private final JobRepository jobRepository;

    /**
     * Start new auto tests job
     *
     * @param jobEntity - auto tests job entity
     */
    public void start(JobEntity jobEntity) {
        log.info("Runs new auto tests job with uuid [{}]", jobEntity.getJobUuid());
        jobEntity.setStarted(LocalDateTime.now());
        jobEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
        jobRepository.save(jobEntity);
        runTests(jobEntity);
        log.info("Auto tests job [{}] has been started", jobEntity.getJobUuid());
    }

    private void runTests(JobEntity jobEntity) {
        var autoTestsRunner = autoTestRunners
                .stream()
                .filter(runner -> runner.getAutoTestType().equals(jobEntity.getAutoTestType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't run tests with type [%s]", jobEntity.getAutoTestType())));
        autoTestsRunner.runTests(jobEntity);
    }
}
