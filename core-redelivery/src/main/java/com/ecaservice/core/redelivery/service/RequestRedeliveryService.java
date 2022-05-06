package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.core.redelivery.config.RedeliveryProperties;
import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.common.web.util.PageHelper.processWithPagination;
import static com.ecaservice.core.redelivery.config.RedeliveryCoreAutoConfiguration.REDELIVERY_LOCK_REGISTRY;

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
    @Locked(lockName = "processNotSentRetryRequests", lockRegistry = REDELIVERY_LOCK_REGISTRY, waitForLock = false)
    public void processNotSentRequests() {
        log.debug("Starting redeliver requests");
        var pageRequest = PageRequest.of(0, redeliveryProperties.getMaxRequests());
        var ids = retryRequestRepository.getNotSentRequestIds(pageRequest);
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Found [{}] not sent requests", ids.size());
            processWithPagination(ids, retryRequestRepository::findByIdIn,
                    pageContent -> pageContent.forEach(this::retryRequest), redeliveryProperties.getPageSize());
        }
        log.debug("Redeliver requests has been finished");
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
