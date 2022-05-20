package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.model.RetryRequestModel;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
     * @param retryRequestModel - retry request model
     */
    public void save(RetryRequestModel retryRequestModel) {
        log.info("Starting to save retry request [{}] into db cache", retryRequestModel);
        var retryRequest = new RetryRequest();
        retryRequest.setRequestType(retryRequestModel.getRequestType());
        retryRequest.setRequestId(retryRequestModel.getRequestId());
        retryRequest.setRequest(retryRequestModel.getRequest());
        retryRequest.setTxId(getMdc(TX_ID));
        retryRequest.setMaxRetries(retryRequestModel.getMaxRetries());
        LocalDateTime retryAt = LocalDateTime.now().plus(retryRequestModel.getMinRetryInterval(), ChronoUnit.MILLIS);
        retryRequest.setRetryAt(retryAt);
        retryRequest.setCreatedAt(LocalDateTime.now());
        retryRequestRepository.save(retryRequest);
        log.info("Retry request [{}] has been saved into db cache", retryRequestModel);
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
