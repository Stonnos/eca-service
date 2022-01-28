package com.ecaservice.load.test.scheduler;

import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.entity.RequestStageType;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.load.test.repository.LoadTestRepository;
import com.ecaservice.load.test.service.executor.TestExecutor;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.common.web.util.PageHelper.processWithPagination;

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
        List<Long> testIds = loadTestRepository.findNewTests();
        processWithPagination(testIds, loadTestRepository::findByIdIn,
                pageContent -> pageContent.forEach(testExecutor::runTest), ecaLoadTestsConfig.getPageSize());
        log.trace("New tests has been processed");
    }

    /**
     * Processes exceeded requests.
     */
    @Scheduled(fixedDelayString = "${eca-load-tests.delaySeconds}000")
    public void processExceededRequests() {
        log.trace("Starting to processed exceeded requests");
        LocalDateTime exceededTime = LocalDateTime.now().minusSeconds(ecaLoadTestsConfig.getRequestTimeoutInSeconds());
        List<Long> exceededIds = evaluationRequestRepository.findExceededRequestIds(exceededTime);
        processWithPagination(exceededIds, evaluationRequestRepository::findByIdIn, pageContent ->
                pageContent.forEach(evaluationRequestEntity -> {
                    evaluationRequestEntity.setStageType(RequestStageType.EXCEEDED);
                    evaluationRequestEntity.setTestResult(TestResult.ERROR);
                    evaluationRequestEntity.setFinished(LocalDateTime.now());
                    evaluationRequestRepository.save(evaluationRequestEntity);
                    log.info("Exceeded request with correlation id [{}]", evaluationRequestEntity.getCorrelationId());
                }), ecaLoadTestsConfig.getPageSize()
        );
        log.trace("Exceeded requests has been processed");
    }

    /**
     * Processes finished tests.
     */
    @Scheduled(fixedDelayString = "${eca-load-tests.delaySeconds}000")
    public void processFinishedTests() {
        log.trace("Starting to processed finished tests");
        List<Long> testIds = loadTestRepository.findFinishedTests();
        processWithPagination(testIds, loadTestRepository::findByIdIn, pageContent ->
                pageContent.forEach(loadTestEntity -> {
                    loadTestEntity.setExecutionStatus(ExecutionStatus.FINISHED);
                    loadTestEntity.setFinished(LocalDateTime.now());
                    loadTestRepository.save(loadTestEntity);
                    log.info("Load test [{}] has been finished", loadTestEntity.getTestUuid());
                }), ecaLoadTestsConfig.getPageSize()
        );
        log.trace("Finished tests has been processed");
    }
}
