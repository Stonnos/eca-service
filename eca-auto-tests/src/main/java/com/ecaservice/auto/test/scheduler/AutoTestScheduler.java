package com.ecaservice.auto.test.scheduler;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.repository.autotest.AutoTestsJobRepository;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.repository.autotest.ExperimentRequestRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.EvaluationRequestService;
import com.ecaservice.auto.test.service.EvaluationResultsProcessor;
import com.ecaservice.auto.test.service.executor.AutoTestExecutor;
import com.ecaservice.auto.test.service.step.TestStepService;
import com.ecaservice.test.common.model.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.common.web.util.PageHelper.processWithPagination;

/**
 * Auto test scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTestScheduler {

    private static final List<ExecutionStatus> FINISHED_EXECUTION_STATUSES = List.of(
            ExecutionStatus.FINISHED,
            ExecutionStatus.ERROR
    );

    private final AutoTestsProperties autoTestsProperties;
    private final AutoTestExecutor autoTestExecutor;
    private final AutoTestJobService autoTestJobService;
    private final EvaluationRequestService evaluationRequestService;
    private final TestStepService testStepService;
    private final EvaluationResultsProcessor evaluationResultsProcessor;
    private final AutoTestsJobRepository autoTestsJobRepository;
    private final ExperimentRequestRepository experimentRequestRepository;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;
    private final BaseTestStepRepository baseTestStepRepository;

    /**
     * Processes new auto tests.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void startNewTestsJobs() {
        log.trace("Starting to processed new tests jobs");
        List<Long> testIds = autoTestsJobRepository.findNewTests();
        processWithPagination(testIds, autoTestsJobRepository::findByIdInOrderByCreated,
                pageContent -> pageContent.forEach(autoTestExecutor::runTests), autoTestsProperties.getPageSize());
        log.trace("New tests has been processed jobs");
    }

    /**
     * Processes finished experiment requests.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processFinishedExperimentRequests() {
        log.trace("Starting to processed finished requests");
        List<Long> finishedIds = experimentRequestRepository.findFinishedRequests();
        processWithPagination(finishedIds, experimentRequestRepository::findByIdInOrderByCreated,
                this::processFinishedRequests, autoTestsProperties.getPageSize());
    }

    /**
     * Processes finished experiment tests.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processFinishedExperimentTests() {
        log.trace("Starting to processed finished requests");
        List<Long> finishedIds = experimentRequestRepository.findFinishedTests(FINISHED_EXECUTION_STATUSES);
        processWithPagination(finishedIds, experimentRequestRepository::findByIdInOrderByCreated,
                this::processFinishedTests, autoTestsProperties.getPageSize());
    }

    /**
     * Processes exceeded test steps.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processExceededTestSteps() {
        log.trace("Starting to processed exceeded test steps");
        LocalDateTime exceededTime = LocalDateTime.now().minusSeconds(autoTestsProperties.getRequestTimeoutInSeconds());
        List<Long> exceededIds = baseTestStepRepository.findExceededStepIds(exceededTime, FINISHED_EXECUTION_STATUSES);
        processWithPagination(exceededIds, baseTestStepRepository::findByIdInOrderByCreated,
                testStepService::exceedTestSteps, autoTestsProperties.getPageSize()
        );
        log.trace("Exceeded test steps has been processed");
    }

    /**
     * Processes exceeded requests.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processExceededRequests() {
        log.trace("Starting to processed exceeded requests");
        LocalDateTime exceededTime = LocalDateTime.now().minusSeconds(autoTestsProperties.getRequestTimeoutInSeconds());
        List<Long> exceededIds =
                baseEvaluationRequestRepository.findExceededRequestIds(exceededTime, FINISHED_EXECUTION_STATUSES);
        processWithPagination(exceededIds, baseEvaluationRequestRepository::findByIdInOrderByCreated, pageContent ->
                pageContent.forEach(evaluationRequestService::exceed), autoTestsProperties.getPageSize()
        );
        log.trace("Exceeded requests has been processed");
    }

    /**
     * Processes finished auto tests jobs.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processFinishedTestJobs() {
        log.trace("Starting to processed finished tests jobs");
        List<Long> testIds = autoTestsJobRepository.findFinishedJobs(FINISHED_EXECUTION_STATUSES);
        processWithPagination(testIds, autoTestsJobRepository::findByIdInOrderByCreated, pageContent ->
                pageContent.forEach(autoTestJobService::finish), autoTestsProperties.getPageSize()
        );
        log.trace("Finished tests jobs has been processed");
    }

    private void processFinishedRequests(List<ExperimentRequestEntity> experimentRequestEntities) {
        experimentRequestEntities.forEach(evaluationResultsProcessor::compareAndMatchExperimentResults);
    }

    private void processFinishedTests(List<ExperimentRequestEntity> experimentRequestEntities) {
        experimentRequestEntities.forEach(experimentRequestEntity -> {
            experimentRequestEntity.setExecutionStatus(ExecutionStatus.FINISHED);
            experimentRequestEntity.setFinished(LocalDateTime.now());
            experimentRequestRepository.save(experimentRequestEntity);
            log.info("Experiment [{}] test execution has been finished", experimentRequestEntity.getRequestId());
        });
    }
}
