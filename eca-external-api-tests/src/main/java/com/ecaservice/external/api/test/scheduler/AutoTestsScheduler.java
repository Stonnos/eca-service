package com.ecaservice.external.api.test.scheduler;

import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.repository.JobRepository;
import com.ecaservice.external.api.test.service.executor.AutoTestsExecutor;
import com.ecaservice.test.common.model.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Auto tests scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoTestsScheduler {

    private static final List<ExecutionStatus> FINISHED_STATUSES = List.of(
            ExecutionStatus.FINISHED,
            ExecutionStatus.ERROR
    );

    private final AutoTestsExecutor autoTestsExecutor;
    private final JobRepository jobRepository;

    /**
     * Processes new tests.
     */
    @Scheduled(fixedDelayString = "${external-api-tests.delaySeconds}000")
    public void processNewTests() {
        List<JobEntity> jobs = jobRepository.findNewJobs();
        if (!CollectionUtils.isEmpty(jobs)) {
            log.info("Found [{}] new auto test jobs to run", jobs.size());
            jobs.forEach(autoTestsExecutor::start);
        }
    }

    /**
     * Processes finished tests.
     */
    @Scheduled(fixedDelayString = "${external-api-tests.delaySeconds}000")
    public void processFinishedTests() {
        log.trace("Starting to processed finished tests");
        List<JobEntity> jobs = jobRepository.findFinishedTests(FINISHED_STATUSES);
        if (!CollectionUtils.isEmpty(jobs)) {
            log.info("Found [{}] finished auto test jobs", jobs.size());
            jobs.forEach(jobEntity -> {
                jobEntity.setExecutionStatus(ExecutionStatus.FINISHED);
                jobEntity.setFinished(LocalDateTime.now());
                jobRepository.save(jobEntity);
                log.info("Auto test [{}] job has been finished", jobEntity.getJobUuid());
            });
        }
        log.trace("Finished tests has been processed");
    }
}
