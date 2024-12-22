package com.ecaservice.auto.test.scheduler;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationResultsTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentResultsTestStepEntity;
import com.ecaservice.auto.test.event.model.EvaluationResultsTestStepEvent;
import com.ecaservice.auto.test.event.model.ExperimentResultsTestStepEvent;
import com.ecaservice.auto.test.repository.autotest.AutoTestsJobRepository;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.repository.autotest.EvaluationResultsTestStepRepository;
import com.ecaservice.auto.test.repository.autotest.ExperimentResultsTestStepRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.EvaluationRequestService;
import com.ecaservice.auto.test.service.executor.AutoTestExecutor;
import com.ecaservice.auto.test.service.step.TestStepService;
import com.ecaservice.test.common.model.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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
    private final EvaluationResultsTestStepRepository evaluationResultsTestStepRepository;

    /**
     * Processes new auto tests.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void startNewTestsJobs() {
        log.trace("Starting to processed new tests jobs");
        processWithPagination(autoTestsJobRepository::findNewTests,
                pageContent -> pageContent.forEach(autoTestExecutor::runTests));
        log.trace("New tests has been processed jobs");
    }

    /**
     * Processes experiment results test steps.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processExperimentResultsTestSteps() {
        log.trace("Starting to process experiment results test steps");
        processWithPagination(experimentResultsTestStepRepository::findStepsToCompareResults,
                this::processExperimentResultsSteps
        );
    }

    /**
     * Processes evaluation results test steps.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processEvaluationResultsTestSteps() {
        log.trace("Starting to process evaluation results test steps");
        processWithPagination(evaluationResultsTestStepRepository::findStepsToCompareResults,
                this::processEvaluationResultsSteps);
    }

    /**
     * Processes finished evaluation requests tests.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processFinishedEvaluationRequestsTests() {
        log.trace("Starting to process finished requests");
        processWithPagination(
                pageable -> baseEvaluationRequestRepository.findFinishedTests(FINISHED_EXECUTION_STATUSES, pageable),
                this::processFinishedEvaluationRequestsTests
        );
    }

    /**
     * Processes exceeded test steps.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processExceededTestSteps() {
        log.trace("Starting to processed exceeded test steps");
        LocalDateTime exceededTime = LocalDateTime.now().minusSeconds(autoTestsProperties.getRequestTimeoutInSeconds());
        processWithPagination(
                pageable -> baseTestStepRepository.findExceededStepIds(exceededTime, FINISHED_EXECUTION_STATUSES,
                        pageable), testStepService::exceedTestSteps
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
        processWithPagination(pageable -> baseEvaluationRequestRepository.findExceededRequestIds(exceededTime,
                        FINISHED_EXECUTION_STATUSES, pageable),
                pageContent -> pageContent.forEach(evaluationRequestService::exceed)
        );
        log.trace("Exceeded requests has been processed");
    }

    /**
     * Processes finished auto tests jobs.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processFinishedTestJobs() {
        log.trace("Starting to processed finished tests jobs");
        processWithPagination(
                pageable -> autoTestsJobRepository.findFinishedJobs(FINISHED_EXECUTION_STATUSES, pageable),
                pageContent -> pageContent.forEach(autoTestJobService::finish)
        );
        log.trace("Finished tests jobs has been processed");
    }

    private void processExperimentResultsSteps(List<ExperimentResultsTestStepEntity> steps) {
        steps.forEach(step ->
                applicationEventPublisher.publishEvent(new ExperimentResultsTestStepEvent(this, step))
        );
    }

    private void processEvaluationResultsSteps(List<EvaluationResultsTestStepEntity> steps) {
        steps.forEach(step ->
                applicationEventPublisher.publishEvent(new EvaluationResultsTestStepEvent(this, step))
        );
    }

    private void processFinishedEvaluationRequestsTests(List<BaseEvaluationRequestEntity> requestEntities) {
        requestEntities.forEach(evaluationRequestService::complete);
    }

    private <T> void processWithPagination(Function<Pageable, Page<T>> nextPageFunction,
                                           Consumer<List<T>> pageContentAction) {
        Pageable pageRequest = PageRequest.of(0, autoTestsProperties.getPageSize());
        Page<T> page;
        do {
            page = nextPageFunction.apply(pageRequest);
            if (page == null || !page.hasContent()) {
                log.debug("No one page has been fetched");
                break;
            } else {
                log.debug("Process page [{}] of [{}] with size [{}]", page.getNumber(), page.getTotalPages(),
                        page.getSize());
                pageContentAction.accept(page.getContent());
            }
        } while (page.hasNext());
    }
}
