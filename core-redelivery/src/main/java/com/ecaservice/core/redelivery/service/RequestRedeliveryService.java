package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.config.RedeliveryProperties;
import com.ecaservice.core.redelivery.entity.RetryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;

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
    private final RetryRequestFetcher retryRequestFetcher;

    /**
     * Retries all not sent requests.
     */
    public void processNotSentRequests() {
        log.debug("Starting retry requests job");
        var pageRequest = PageRequest.of(0, redeliveryProperties.getPageSize());
        List<RetryRequest> retryRequests;
        while (!(retryRequests = retryRequestFetcher.lockNextNotSentRequests(pageRequest)).isEmpty()) {
            retryRequests.forEach(this::retryRequest);
        }
        log.debug("Retry requests job has been finished");
    }

    private void retryRequest(RetryRequest retryRequest) {
        putMdc(TX_ID, retryRequest.getTxId());
        try {
            retryService.retry(retryRequest);
        } catch (Exception ex) {
            log.error("Error while sending retry request [{}] request with id [{}]: {}", retryRequest.getRequestType(),
                    retryRequest.getId(), ex.getMessage());
        }
    }
}
