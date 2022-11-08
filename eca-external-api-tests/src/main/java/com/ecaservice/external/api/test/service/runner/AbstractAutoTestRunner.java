package com.ecaservice.external.api.test.service.runner;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.dto.AutoTestType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.model.AbstractTestDataModel;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.service.JobService;
import com.ecaservice.external.api.test.service.TestWorkerService;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Abstract auto test runner.
 *
 * @param <A>  - auto test entity generic type
 * @param <TD> - test data generic type
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAutoTestRunner<A extends AutoTestEntity, TD extends AbstractTestDataModel> {

    @Getter
    private final AutoTestType autoTestType;
    private final ExternalApiTestsConfig externalApiTestsConfig;
    private final JobService jobService;
    private final TestWorkerService testWorkerService;
    private final AutoTestRepository autoTestRepository;

    /**
     * Runs auto tests for job.
     *
     * @param jobEntity - job entity
     */
    public void runTests(JobEntity jobEntity) {
        var executor = Executors.newFixedThreadPool(jobEntity.getNumThreads());
        List<TD> testDataModels = getTestDataModels();
        try {
            testDataModels.forEach(testDataModel -> {
                var autoTestEntity = createAutoTestEntity(testDataModel);
                populateAndSaveAutoTestEntityCommonData(autoTestEntity, jobEntity, testDataModel);
                Runnable task = createTask(autoTestEntity.getId(), testDataModel);
                executor.submit(task);
            });
            executor.shutdown();
            if (!executor.awaitTermination(externalApiTestsConfig.getWorkerThreadTimeoutInSeconds(),
                    TimeUnit.SECONDS)) {
                String errorMessage =
                        String.format("Worker thread timeout occurred for auto test job [%s]", jobEntity.getJobUuid());
                log.error(errorMessage);
                jobService.finishWithError(jobEntity, errorMessage);
                executor.shutdownNow();
            }
        } catch (Exception ex) {
            log.error("There was an error while auto test job [{}]: {}", jobEntity.getJobUuid(),
                    ex.getMessage(), ex);
            jobService.finishWithError(jobEntity, ex.getMessage());
        }
    }

    protected abstract List<TD> getTestDataModels();

    protected abstract A createAutoTestEntity(TD testDataModel);

    private void populateAndSaveAutoTestEntityCommonData(A autoTestEntity,
                                                         JobEntity jobEntity,
                                                         TD testDataModel) {
        autoTestEntity.setJob(jobEntity);
        autoTestEntity.setDisplayName(testDataModel.getDisplayName());
        autoTestEntity.setExecutionStatus(ExecutionStatus.NEW);
        autoTestEntity.setTestResult(TestResult.UNKNOWN);
        autoTestEntity.setCreated(LocalDateTime.now());
        autoTestRepository.save(autoTestEntity);
    }

    private Runnable createTask(long testId, TD testDataModel) {
        return () -> testWorkerService.execute(testId, testDataModel);
    }
}
