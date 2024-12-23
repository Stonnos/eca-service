package com.ecaservice.load.test.scheduler;

import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.entity.RequestStageType;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import com.ecaservice.load.test.service.executor.TestExecutor;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * Load test scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoadTestScheduler {

    private final EcaLoadTestsConfig ecaLoadTestsConfig;
    private final TestExecutor testExecutor;
    private final LoadTestRepository loadTestRepository;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Processes new tests.
     */
    @Scheduled(fixedDelayString = "${eca-load-tests.delaySeconds}000")
    public void processNewTests() {
        log.trace("Starting to processed new tests");
        processWithPagination(loadTestRepository::findNewTests,
                loadTestEntityList -> loadTestEntityList.forEach(testExecutor::runTest)
        );
        log.trace("New tests has been processed");
    }

    /**
     * Processes exceeded requests.
     */
    @Scheduled(fixedDelayString = "${eca-load-tests.delaySeconds}000")
    public void processExceededRequests() {
        log.trace("Starting to processed exceeded requests");
        LocalDateTime exceededTime = LocalDateTime.now().minusSeconds(ecaLoadTestsConfig.getRequestTimeoutInSeconds());
        processWithPagination(pageable -> evaluationRequestRepository.findExceededRequestIds(exceededTime, pageable),
                this::processExceededRequests);
        log.trace("Exceeded requests has been processed");
    }

    /**
     * Processes finished tests.
     */
    @Scheduled(fixedDelayString = "${eca-load-tests.delaySeconds}000")
    public void processFinishedTests() {
        log.trace("Starting to processed finished tests");
        processWithPagination(loadTestRepository::findFinishedTests, this::processFinishedJobsTests);
        log.trace("Finished tests has been processed");
    }

    private void processExceededRequests(List<EvaluationRequestEntity> evaluationRequestEntities) {
        evaluationRequestEntities.forEach(evaluationRequestEntity -> {
            evaluationRequestEntity.setStageType(RequestStageType.EXCEEDED);
            evaluationRequestEntity.setTestResult(TestResult.ERROR);
            evaluationRequestEntity.setFinished(LocalDateTime.now());
            evaluationRequestRepository.save(evaluationRequestEntity);
            log.info("Exceeded request with correlation id [{}]",
                    evaluationRequestEntity.getCorrelationId());
        });
    }

    private void processFinishedJobsTests(List<LoadTestEntity> loadTestEntityList) {
        loadTestEntityList.forEach(loadTestEntity -> {
            loadTestEntity.setExecutionStatus(ExecutionStatus.FINISHED);
            loadTestEntity.setFinished(LocalDateTime.now());
            loadTestRepository.save(loadTestEntity);
            log.info("Load test [{}] has been finished", loadTestEntity.getTestUuid());
        });
    }

    private <T> void processWithPagination(Function<Pageable, Page<T>> nextPageFunction,
                                           Consumer<List<T>> pageContentAction) {
        Pageable pageRequest = PageRequest.of(0, ecaLoadTestsConfig.getPageSize());
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
