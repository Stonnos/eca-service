package com.ecaservice.server.service.ers;

import com.ecaservice.core.lock.annotation.TryLocked;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.ErsRetryRequest;
import com.ecaservice.server.repository.ErsRetryRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.ecaservice.server.config.EcaServiceConfiguration.EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN;
import static com.ecaservice.server.util.PageHelper.processWithPagination;

/**
 * Ers requests scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErsRedeliveryService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ErsConfig ersConfig;
    private final ErsRequestService ersRequestService;
    private final ErsErrorHandler ersErrorHandler;
    private final ErsRetryRequestCacheService ersRetryRequestCacheService;
    private final ErsRetryRequestRepository ersRetryRequestRepository;

    /**
     * Retry to send ers requests.
     */
    @TryLocked(lockName = "resendErsRequestsJob", lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN)
    public void resendErsRequests() {
        log.info("Starting to resend ers requests");
        var ids = ersRetryRequestRepository.getNotSentIds();
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Found [{}] not sent ers requests", ids.size());
            processWithPagination(ids, ersRetryRequestRepository::findByIdIn, this::processPage,
                    ersConfig.getPageSize());
        }
        log.info("Ers requests resending has been finished");
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
                ersErrorHandler.handleErrorRequest(ersRetryRequest.getErsRequest(), ErsResponseStatus.ERROR,
                        ex.getMessage());
                ersRetryRequestCacheService.evict(ersRetryRequest.getErsRequest());
            }
        }
    }
}
