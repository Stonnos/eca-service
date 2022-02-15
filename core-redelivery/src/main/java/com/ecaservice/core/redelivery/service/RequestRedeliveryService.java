package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.config.RedeliveryProperties;
import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.ecaservice.common.web.util.PageHelper.processWithPagination;

/**
 * Retry requests redelivery service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestRedeliveryService {

    private final RedeliveryProperties redeliveryProperties;
    private final RetryService retryService;
    private final RetryRequestRepository retryRequestRepository;

    /**
     * Retries all not sent requests.
     */
    public void processNotSentRequests() {
        log.debug("Starting redeliver requests");
        var ids = retryRequestRepository.getNotSentRequestIds();
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Found [{}] not sent requests", ids.size());
            processWithPagination(ids, retryRequestRepository::findByIdIn,
                    pageContent -> pageContent.forEach(this::retryRequest), redeliveryProperties.getPageSize());
        }
        log.debug("Redeliver requests has been finished");
    }

    private void retryRequest(RetryRequest retryRequest) {
        try {
            retryService.retry(retryRequest);
        } catch (Exception ex) {
            log.error("Error while sending retry request [{}] request with id [{}]: {}", retryRequest.getRequestType(),
                    retryRequest.getId(), ex.getMessage());
        }
    }
}
