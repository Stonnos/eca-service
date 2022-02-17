package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.getMdc;

/**
 * Implements retry request cache service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetryRequestCacheService {

    private final RetryRequestRepository retryRequestRepository;

    /**
     * Saves retry request into db.
     *
     * @param requestType - request type (code)
     * @param request     - request body
     * @param maxRetries  - maximum retries
     */
    public void save(String requestType, String request, int maxRetries) {
        log.info("Starting to save retry request [{}] into db cache", requestType);
        var retryRequest = new RetryRequest();
        retryRequest.setRequestType(requestType);
        retryRequest.setRequest(request);
        retryRequest.setTxId(getMdc(TX_ID));
        retryRequest.setMaxRetries(maxRetries);
        retryRequest.setCreatedAt(LocalDateTime.now());
        retryRequestRepository.save(retryRequest);
        log.info("Retry request [{}] has been saved into db cache", requestType);
    }

    /**
     * Deletes retry request from db.
     *
     * @param retryRequest - retry request entity
     */
    public void delete(RetryRequest retryRequest) {
        log.info("Starting to delete retry request [{}] with id [{}]", retryRequest.getRequestType(),
                retryRequest.getId());
        retryRequestRepository.delete(retryRequest);
        log.info("Retry request [{}] with id [{}] has been removed from cache", retryRequest.getRequestType(),
                retryRequest.getId());
    }
}
