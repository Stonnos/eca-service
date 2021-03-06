package com.ecaservice.external.api.test.service.executor;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.ExecutionStatus;
import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.entity.TestResult;
import com.ecaservice.external.api.test.model.TestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.repository.JobRepository;
import com.ecaservice.external.api.test.service.TestDataService;
import com.ecaservice.external.api.test.service.TestWorkerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Auto tests executor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTestsExecutor {

    private final ExternalApiTestsConfig externalApiTestsConfig;
    private final TestWorkerService testWorkerService;
    private final TestDataService testDataService;
    private final JobRepository jobRepository;
    private final AutoTestRepository autoTestRepository;

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
        log.info("Auto tests job [{}] has been finished", jobEntity.getJobUuid());
    }

    private void runTests(JobEntity jobEntity) {
        ThreadPoolTaskExecutor executor = initializeThreadPoolTaskExecutor(jobEntity.getNumThreads());
        List<TestDataModel> testDataModels = testDataService.getTestDataModels();
        CountDownLatch countDownLatch = new CountDownLatch(testDataModels.size());
        try {
            testDataModels.forEach(testDataModel -> {
                AutoTestEntity autoTestEntity = createAndSaveAutoTest(jobEntity, testDataModel);
                Runnable task = createTask(autoTestEntity.getId(), testDataModel, countDownLatch);
                executor.submit(task);
            });
            if (!countDownLatch.await(externalApiTestsConfig.getWorkerThreadTimeoutInSeconds(), TimeUnit.SECONDS)) {
                log.warn("Worker thread timeout occurred for auto test job [{}]", jobEntity.getJobUuid());
                jobEntity.setExecutionStatus(ExecutionStatus.TIMEOUT);
                return;
            }
            jobEntity.setExecutionStatus(ExecutionStatus.FINISHED);
        } catch (Exception ex) {
            log.error("There was an error while auto test job [{}]: {}", jobEntity.getJobUuid(),
                    ex.getMessage(), ex);
            failed(jobEntity, ex.getMessage());
        } finally {
            jobEntity.setFinished(LocalDateTime.now());
            jobRepository.save(jobEntity);
            executor.shutdown();
        }
    }

    private AutoTestEntity createAndSaveAutoTest(JobEntity jobEntity, TestDataModel testDataModel) {
        AutoTestEntity autoTestEntity = new AutoTestEntity();
        autoTestEntity.setJob(jobEntity);
        autoTestEntity.setDisplayName(testDataModel.getDisplayName());
        autoTestEntity.setExecutionStatus(ExecutionStatus.NEW);
        autoTestEntity.setTestResult(TestResult.UNKNOWN);
        autoTestEntity.setCreated(LocalDateTime.now());
        return autoTestRepository.save(autoTestEntity);
    }

    private void failed(JobEntity jobEntity, String errorMessage) {
        jobEntity.setDetails(errorMessage);
        jobEntity.setExecutionStatus(ExecutionStatus.ERROR);
    }

    private Runnable createTask(long testId, TestDataModel testDataModel, CountDownLatch countDownLatch) {
        return () -> testWorkerService.execute(testId, testDataModel, countDownLatch);
    }

    private ThreadPoolTaskExecutor initializeThreadPoolTaskExecutor(int poolSize) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(poolSize);
        threadPoolTaskExecutor.setMaxPoolSize(poolSize);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
