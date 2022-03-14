package com.ecaservice.auto.test.scheduler;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.repository.autotest.AutoTestsJobRepository;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.ExperimentRequestRepository;
import com.ecaservice.auto.test.service.AutoTestJobService;
import com.ecaservice.auto.test.service.EvaluationRequestService;
import com.ecaservice.auto.test.service.EvaluationResultsProcessor;
import com.ecaservice.auto.test.service.executor.AutoTestExecutor;
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

    private static final List<RequestStageType> FINISHED_STAGES = List.of(
            RequestStageType.COMPLETED,
            RequestStageType.ERROR,
            RequestStageType.EXCEEDED
    );

    private final AutoTestsProperties autoTestsProperties;
    private final AutoTestExecutor autoTestExecutor;
    private final AutoTestJobService autoTestJobService;
    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationResultsProcessor evaluationResultsProcessor;
    private final AutoTestsJobRepository autoTestsJobRepository;
    private final ExperimentRequestRepository experimentRequestRepository;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;

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
     * Processes exceeded requests.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processExceededRequests() {
        log.trace("Starting to processed exceeded requests");
        LocalDateTime exceededTime = LocalDateTime.now().minusSeconds(autoTestsProperties.getRequestTimeoutInSeconds());
        List<Long> exceededIds = baseEvaluationRequestRepository.findExceededRequestIds(exceededTime, FINISHED_STAGES);
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
        List<Long> testIds = autoTestsJobRepository.findFinishedJobs(FINISHED_STAGES);
        processWithPagination(testIds, autoTestsJobRepository::findByIdInOrderByCreated, pageContent ->
                pageContent.forEach(autoTestJobService::finish), autoTestsProperties.getPageSize()
        );
        log.trace("Finished tests jobs has been processed");
    }

    private void processFinishedRequests(List<ExperimentRequestEntity> experimentRequestEntities) {
        experimentRequestEntities.forEach(evaluationResultsProcessor::compareAndMatchExperimentResults);
    }
}
