package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.config.RedeliveryProperties;
import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.time.LocalDateTime;

/**
 * Retry requests fetcher.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetryRequestFetcher {

    private final RedeliveryProperties redeliveryProperties;
    private final RetryRequestRepository retryRequestRepository;

    /**
     * Gets next retry requests for processing and sets a lock to prevent other threads from receiving the same data for
     * processing.
     *
     * @param pageable - pageable object
     * @return retry requests page
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Page<RetryRequest> getNotSentRequests(Pageable pageable) {
        log.debug("Starting to get next retry requests to process");
        Page<RetryRequest> retryRequests = retryRequestRepository.getNotSentRequests(LocalDateTime.now(), pageable);
        if (retryRequests.hasContent()) {
            // Sets a lock to prevent other threads from receiving the same data for processing
            LocalDateTime lockedTtl = LocalDateTime.now().plusSeconds(redeliveryProperties.getLockTtlSeconds());
            var ids = retryRequests.getContent()
                    .stream()
                    .map(RetryRequest::getId)
                    .toList();
            retryRequestRepository.lock(ids, lockedTtl);
            log.info("[{}] retry requests to process has been fetched", retryRequests.getNumberOfElements());
        }
        log.debug("Next retry requests to process has been fetched");
        return retryRequests;
    }
}
