package com.ecaservice.auto.test.scheduler;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.ExperimentRequestStageType;
import com.ecaservice.auto.test.repository.AutoTestsJobRepository;
import com.ecaservice.auto.test.repository.ExperimentRequestRepository;
import com.ecaservice.auto.test.service.ExperimentRequestService;
import com.ecaservice.auto.test.service.api.EcaServerClient;
import com.ecaservice.auto.test.service.executor.AutoTestExecutor;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import eca.dataminer.AbstractExperiment;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Auto test scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTestScheduler {

    private static final List<ExperimentRequestStageType> FINISHED_STAGES = List.of(
            ExperimentRequestStageType.COMPLETED,
            ExperimentRequestStageType.ERROR,
            ExperimentRequestStageType.EXCEEDED
    );

    private static final String SLASH_SEPARATOR = "/";

    private final AutoTestsProperties autoTestsProperties;
    private final AutoTestExecutor autoTestExecutor;
    private final ExperimentRequestService experimentRequestService;
    private final EcaServerClient ecaServerClient;
    private final AutoTestsJobRepository autoTestsJobRepository;
    private final ExperimentRequestRepository experimentRequestRepository;

    /**
     * Processes new auto tests.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void startNewTestsJobs() {
        log.trace("Starting to processed new tests jobs");
        List<Long> testIds = autoTestsJobRepository.findNewTests();
        processPaging(testIds, autoTestsJobRepository::findByIdIn,
                pageContent -> pageContent.forEach(autoTestExecutor::runTests));
        log.trace("New tests has been processed jobs");
    }

    /**
     * Processes finished requests.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processFinishedRequests() {
        log.trace("Starting to processed finished requests");
        List<Long> finishedIds = experimentRequestRepository.findFinishedRequests();
        processPaging(finishedIds, experimentRequestRepository::findByIdIn, pageContent ->
                pageContent.forEach(experimentRequestEntity -> {
                    try {
                        log.info("Starting to download experiment [{}] history",
                                experimentRequestEntity.getRequestId());
                        String token = StringUtils.substringAfterLast(experimentRequestEntity.getDownloadUrl(),
                                SLASH_SEPARATOR);
                        Resource modelResource = ecaServerClient.downloadModel(token);
                        @Cleanup InputStream inputStream = modelResource.getInputStream();
                        AbstractExperiment<?> experimentHistory = SerializationUtils.deserialize(inputStream);
                        log.info("Experiment [{}] history has been downloaded", experimentRequestEntity.getRequestId());
                        experimentRequestService.processExperimentHistory(experimentRequestEntity, experimentHistory);
                    } catch (Exception ex) {
                        log.error("There was an error while process finished experiment request [{}]: {}",
                                experimentRequestEntity.getRequestId(), ex.getMessage());
                        experimentRequestService.finishWithError(experimentRequestEntity, ex.getMessage());
                    }
                })
        );
    }

    /**
     * Processes exceeded requests.
     */
    @Scheduled(fixedDelayString = "${auto-tests.delaySeconds}000")
    public void processExceededRequests() {
        log.trace("Starting to processed exceeded requests");
        LocalDateTime exceededTime = LocalDateTime.now().minusSeconds(autoTestsProperties.getRequestTimeoutInSeconds());
        List<Long> exceededIds = experimentRequestRepository.findExceededRequestIds(exceededTime, FINISHED_STAGES);
        processPaging(exceededIds, experimentRequestRepository::findByIdIn, pageContent ->
                pageContent.forEach(experimentRequestEntity -> {
                    experimentRequestEntity.setExecutionStatus(ExecutionStatus.ERROR);
                    experimentRequestEntity.setStageType(ExperimentRequestStageType.EXCEEDED);
                    experimentRequestEntity.setTestResult(TestResult.ERROR);
                    experimentRequestEntity.setDetails(String.format("Request timeout exceeded after [%d] seconds!",
                            autoTestsProperties.getRequestTimeoutInSeconds()));
                    experimentRequestEntity.setFinished(LocalDateTime.now());
                    experimentRequestRepository.save(experimentRequestEntity);
                    log.info("Exceeded request with correlation id [{}]", experimentRequestEntity.getCorrelationId());
                })
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
        processPaging(testIds, autoTestsJobRepository::findByIdIn, pageContent ->
                pageContent.forEach(autoTestsJobEntity -> {
                    autoTestsJobEntity.setExecutionStatus(ExecutionStatus.FINISHED);
                    autoTestsJobEntity.setFinished(LocalDateTime.now());
                    autoTestsJobRepository.save(autoTestsJobEntity);
                    log.info("Auto tests job [{}] has been finished", autoTestsJobEntity.getJobUuid());
                })
        );
        log.trace("Finished tests jobs has been processed");
    }

    private <T> void processPaging(List<Long> ids,
                                   BiFunction<List<Long>, Pageable, Page<T>> nextPageFunction,
                                   Consumer<List<T>> pageContentAction) {
        Pageable pageRequest = PageRequest.of(0, autoTestsProperties.getPageSize());
        Page<T> page;
        do {
            page = nextPageFunction.apply(ids, pageRequest);
            if (page == null || !page.hasContent()) {
                log.trace("No one requests has been fetched");
                break;
            } else {
                pageContentAction.accept(page.getContent());
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }
}
