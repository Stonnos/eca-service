package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.core.redelivery.config.RedeliveryProperties;
import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    private final RetryRequestRepository retryRequestRepository;

    /**
     * Retries all not sent requests.
     */
    @Locked(lockName = "processNotSentRetryRequests", waitForLock = false)
    public void processNotSentRequests() {
        log.debug("Starting retry requests job");
        var pageRequest = PageRequest.of(0, redeliveryProperties.getPageSize());
        Page<RetryRequest> page;
        do {
            page = retryRequestRepository.getNotSentRequests(LocalDateTime.now(), pageRequest);
            if (page == null || !page.hasContent()) {
                log.debug("No one page has been fetched");
                break;
            } else {
                log.info("Process next retry requests page [{}] of [{}] with size [{}]", page.getNumber(),
                        page.getTotalPages(), page.getSize());
                page.getContent().forEach(this::retryRequest);
            }
        } while (page.hasNext());
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
