package com.ecaservice.external.api.scheduler;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.service.RequestStageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Processes exceeded requests.
     */
    @Scheduled(fixedDelayString = "${external-api.delaySeconds}000")
    public void processExceededRequests() {
        log.trace("Starting to processed exceeded requests");
        LocalDateTime now = LocalDateTime.now();
        Pageable pageRequest = PageRequest.of(0, externalApiConfig.getBatchSize());
        Page<EcaRequestEntity> page;
        do {
            page = ecaRequestRepository.findExceededRequestIds(now, pageRequest);
            if (page == null || !page.hasContent()) {
                log.debug("No one page has been fetched");
                break;
            } else {
                log.debug("Process page [{}] of [{}] with size [{}]", page.getNumber(), page.getTotalPages(),
                        page.getSize());
                page.getContent().forEach(requestStageHandler::handleExceeded);
            }
        } while (page.hasNext());
        log.trace("Exceeded requests has been processed");
    }
}
