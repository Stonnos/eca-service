package com.ecaservice.server.service.scheduler;

import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.model.entity.ErsRetryRequest;
import com.ecaservice.server.repository.ErsRetryRequestRepository;
import com.ecaservice.server.service.ers.ErsRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.ecaservice.server.util.PageHelper.processWithPagination;

/**
 * Ers requests scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "${ers.redelivery}", havingValue = "true")
@RequiredArgsConstructor
public class ErsScheduler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ErsConfig ersConfig;
    private final ErsRequestService ersRequestService;
    private final ErsRetryRequestRepository ersRetryRequestRepository;

    /**
     * Retry to send ers requests.
     */
    @Scheduled(fixedDelayString = "${ers.redeliveryIntervalSeconds}000")
    public void resendErsRequests() {
        log.info("Starting ers requests resending job");
        var ids = ersRetryRequestRepository.getNotSentIds();
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Found [{}] not sent ers requests", ids.size());
            processWithPagination(ids, ersRetryRequestRepository::findByIdIn, this::processPage,
                    ersConfig.getPageSize());
        }
        log.info("Ers requests resending job has been finished");
    }

    private void processPage(List<ErsRetryRequest> ersRetryRequests) {
        for (ErsRetryRequest ersRetryRequest : ersRetryRequests) {
            try {
                log.info("Resend ers request [{}]", ersRetryRequest.getErsRequest().getRequestId());
                var evaluationResultsRequest = OBJECT_MAPPER.readValue(ersRetryRequest.getJsonRequest(),
                        EvaluationResultsRequest.class);
                ersRequestService.saveEvaluationResults(evaluationResultsRequest, ersRetryRequest.getErsRequest());
            } catch (Exception ex) {
                log.error("There was an error while resend ers request [{}]: {}",
                        ersRetryRequest.getErsRequest().getRequestId(), ex.getMessage());
            }
        }
    }
}
