package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.config.RedeliveryProperties;
import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

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
     * @return retry requests list
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<RetryRequest> lockNextNotSentRequests(Pageable pageable) {
        log.debug("Starting to get next retry requests to process");
        List<RetryRequest> retryRequests = retryRequestRepository.getNotSentRequests(LocalDateTime.now(), pageable);
        if (!CollectionUtils.isEmpty(retryRequests)) {
            // Sets a lock to prevent other threads from receiving the same data for processing
            LocalDateTime lockedTtl = LocalDateTime.now().plusSeconds(redeliveryProperties.getLockTtlSeconds());
            var ids = retryRequests.stream()
                    .map(RetryRequest::getId)
                    .toList();
            retryRequestRepository.lock(ids, lockedTtl);
            log.info("[{}] retry requests to process has been fetched", retryRequests.size());
        }
        log.debug("Next retry requests to process has been fetched");
        return retryRequests;
    }
}
