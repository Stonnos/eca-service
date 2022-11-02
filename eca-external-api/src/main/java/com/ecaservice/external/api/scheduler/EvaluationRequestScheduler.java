package com.ecaservice.external.api.scheduler;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.service.RequestStageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.common.web.util.PageHelper.processWithPagination;

/**
 * Evaluation request scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationRequestScheduler {

    private final ExternalApiConfig externalApiConfig;
    private final RequestStageHandler requestStageHandler;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Processes exceeded requests.
     */
    @Scheduled(fixedDelayString = "${external-api.delaySeconds}000")
    public void processExceededRequests() {
        log.trace("Starting to processed exceeded requests");
        LocalDateTime exceededTime =
                LocalDateTime.now().minusMinutes(externalApiConfig.getEvaluationRequestTimeoutMinutes());
        List<Long> exceededIds = evaluationRequestRepository.findExceededRequestIds(exceededTime);
        processWithPagination(exceededIds, evaluationRequestRepository::findByIdIn,
                pageContent -> pageContent.forEach(requestStageHandler::handleExceeded),
                externalApiConfig.getBatchSize()
        );
        log.trace("Exceeded requests has been processed");
    }
}
