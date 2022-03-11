package com.ecaservice.auto.test.service.executor;

import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.repository.autotest.AutoTestsJobRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.runner.AbstractAutoTestRunner;
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
public class AutoTestExecutor {

    private final AutoTestJobService autoTestJobService;
    private final List<AbstractAutoTestRunner> autoTestRunners;
    private final AutoTestsJobRepository autoTestsJobRepository;

    /**
     * Runs auto tests.
     *
     * @param autoTestsJobEntity - load test entity
     */
    public void runTests(AutoTestsJobEntity autoTestsJobEntity) {
        log.info("Runs new tests job with uuid [{}]", autoTestsJobEntity.getJobUuid());
        autoTestsJobEntity.setStarted(LocalDateTime.now());
        autoTestsJobEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
        autoTestsJobRepository.save(autoTestsJobEntity);
        internalRunTests(autoTestsJobEntity);
        log.info("Auto tests job [{}] has been started", autoTestsJobEntity.getJobUuid());
    }

    private void internalRunTests(AutoTestsJobEntity autoTestsJobEntity) {
        try {
            var autoTestRunner = autoTestRunners.stream()
                    .filter(runner -> runner.canRun(autoTestsJobEntity))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("Not implemented auto tests type [%s] to run",
                                    autoTestsJobEntity.getAutoTestType())));
            autoTestRunner.run(autoTestsJobEntity);
        } catch (Exception ex) {
            log.error("There was an error while sending requests for job [{}]: {}", autoTestsJobEntity.getJobUuid(),
                    ex.getMessage());
            autoTestJobService.finishWithError(autoTestsJobEntity, ex.getMessage());
        }
    }
}
