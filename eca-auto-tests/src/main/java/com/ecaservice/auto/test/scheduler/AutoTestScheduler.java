package com.ecaservice.auto.test.scheduler;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentResultsTestStepEntity;
import com.ecaservice.auto.test.event.model.ExperimentResultsTestStepEvent;
import com.ecaservice.auto.test.repository.autotest.AutoTestsJobRepository;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.repository.autotest.ExperimentResultsTestStepRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.EvaluationRequestService;
import com.ecaservice.auto.test.service.executor.AutoTestExecutor;
import com.ecaservice.auto.test.service.step.TestStepService;
import com.ecaservice.test.common.model.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AutoTestExecutor autoTestExecutor;
    private final AutoTestJobService autoTestJobService;
    private final EvaluationRequestService evaluationRequestService;
    private final TestStepService testStepService;
    private final AutoTestsJobRepository autoTestsJobRepository;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;
    private final BaseTestStepRepository baseTestStepRepository;
    private final ExperimentResultsTestStepRepository experimentResultsTestStepRepository;

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
     * Processes experiment results test steps.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processExperimentResultsTestSteps() {
        log.trace("Starting to process experiment results test steps");
        List<Long> ids = experimentResultsTestStepRepository.findStepsToCompareResults();
        processWithPagination(ids, experimentResultsTestStepRepository::findByIdInOrderByCreated,
                this::processExperimentResultsSteps, autoTestsProperties.getPageSize());
    }

    /**
     * Processes finished evaluation requests tests.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processFinishedEvaluationRequestsTests() {
        log.trace("Starting to process finished requests");
        List<Long> finishedIds = baseEvaluationRequestRepository.findFinishedTests(FINISHED_EXECUTION_STATUSES);
        processWithPagination(finishedIds, baseEvaluationRequestRepository::findByIdInOrderByCreated,
                this::processFinishedEvaluationRequestsTests, autoTestsProperties.getPageSize());
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

    private void processExperimentResultsSteps(List<ExperimentResultsTestStepEntity> steps) {
        steps.forEach(step ->
                applicationEventPublisher.publishEvent(new ExperimentResultsTestStepEvent(this, step))
        );
    }

    private void processFinishedEvaluationRequestsTests(List<BaseEvaluationRequestEntity> requestEntities) {
        requestEntities.forEach(evaluationRequestService::complete);
    }
}
