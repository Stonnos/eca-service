package com.ecaservice.server.service.ers;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.redelivery.callback.RetryCallback;
import com.ecaservice.core.redelivery.model.RetryContext;
import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.repository.ErsRequestRepository;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.stream.Stream;

/**
 * Ers retry callback.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ErsRetryCallback implements RetryCallback {

    private final Class<?>[] notFatalExceptions = new Class<?>[] {
            FeignException.ServiceUnavailable.class,
            RetryableException.class
    };

    private final ErsErrorHandler ersErrorHandler;
    private final ErsRequestRepository ersRequestRepository;

    @Override
    public void onSuccess(RetryContext retryContext) {
        var ersRequest = getErsRequest(retryContext);
        log.info("Starting to handle success ers request [{}]", ersRequest.getRequestId());
        ersRequest.setResponseStatus(ErsResponseStatus.SUCCESS);
        ersRequestRepository.save(ersRequest);
        log.info("Ers request [{}] has been updated with success response status", ersRequest.getRequestId());
    }

    @Override
    public void onRetryExhausted(RetryContext retryContext) {
        var ersRequest = getErsRequest(retryContext);
        log.info("Starting to handle exhausted ers request [{}]", ersRequest.getRequestId());
        ersErrorHandler.handleErrorRequest(ersRequest, ErsResponseStatus.ERROR,
                String.format("Retries exhausted. Retries [%d] of [%d]", retryContext.getCurrentRetries(),
                        retryContext.getMaxRetries()));
    }

    @Override
    public void onError(RetryContext retryContext, Exception ex) {
        var ersRequest = getErsRequest(retryContext);
        log.info("Starting to handle ers error request [{}]", ersRequest.getRequestId());
        if (notFatal(ex)) {
            ersErrorHandler.handleErrorRequest(ersRequest, ErsResponseStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        } else if (ex instanceof FeignException.BadRequest) {
            FeignException.BadRequest badRequest = (FeignException.BadRequest) ex;
            ersErrorHandler.handleBadRequest(ersRequest, badRequest);
        } else {
            ersErrorHandler.handleErrorRequest(ersRequest, ErsResponseStatus.ERROR, ex.getMessage());
        }
    }

    private ErsRequest getErsRequest(RetryContext retryContext) {
        Assert.notNull(retryContext.getRequestId(), "Expected not empty ers request id");
        return ersRequestRepository.findByRequestId(retryContext.getRequestId())
                .orElseThrow(() -> new EntityNotFoundException(ErsRequest.class, retryContext.getRequestId()));
    }

    private boolean notFatal(Exception ex) {
        return Stream.of(notFatalExceptions)
                .anyMatch(notFatalException -> notFatalException.isAssignableFrom(ex.getClass()));
    }
}
