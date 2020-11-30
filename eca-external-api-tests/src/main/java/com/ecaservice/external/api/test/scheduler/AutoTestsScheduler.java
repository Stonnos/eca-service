package com.ecaservice.external.api.test.scheduler;

import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.repository.JobRepository;
import com.ecaservice.external.api.test.service.executor.AutoTestsExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
}
