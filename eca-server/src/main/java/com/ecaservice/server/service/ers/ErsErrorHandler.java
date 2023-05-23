package com.ecaservice.server.service.ers;

import com.ecaservice.ers.dto.ErsErrorCode;
import com.ecaservice.server.mapping.ErsResponseStatusMapper;
import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.repository.ErsRequestRepository;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

import static com.ecaservice.common.web.util.ValidationErrorHelper.getFirstErrorCodeAsEnum;
import static com.ecaservice.common.web.util.ValidationErrorHelper.retrieveValidationErrors;

/**
 * Ers service error handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErsErrorHandler {

    private final Class<?>[] notFatalExceptions = new Class<?>[] {
            FeignException.ServiceUnavailable.class,
            RetryableException.class
    };

    private final ErsResponseStatusMapper ersResponseStatusMapper;
    private final ErsRequestRepository ersRequestRepository;

    /**
     * Handles error request.
     *
     * @param ersRequest     - ers request
     * @param responseStatus - response status
     * @param errorMessage   - error message
     */
    public void handleErrorRequest(ErsRequest ersRequest, ErsResponseStatus responseStatus, String errorMessage) {
        ersRequest.setResponseStatus(responseStatus);
        ersRequest.setDetails(errorMessage);
        ersRequestRepository.save(ersRequest);
        log.info("Ers request [{}] has been updated with status [{}]", ersRequest.getRequestId(), responseStatus);
    }

    /**
     * Handles error request.
     *
     * @param ersRequest - ers request
     * @param ex         - exception
     */
    public void handleError(ErsRequest ersRequest, Exception ex) {
        log.info("Starting to handle ers error request [{}]", ersRequest.getRequestId());
        if (notFatal(ex)) {
            handleErrorRequest(ersRequest, ErsResponseStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        } else if (ex instanceof FeignException.BadRequest) {
            FeignException.BadRequest badRequest = (FeignException.BadRequest) ex;
            handleBadRequest(ersRequest, badRequest);
        } else {
            handleErrorRequest(ersRequest, ErsResponseStatus.ERROR, ex.getMessage());
        }
    }

    /**
     * Handles bad request error.
     *
     * @param ersRequest   - ers request
     * @param badRequestEx - bad request exception
     * @return ers error code
     */
    public ErsErrorCode handleBadRequest(ErsRequest ersRequest, FeignException.BadRequest badRequestEx) {
        try {
            var validationErrors = retrieveValidationErrors(badRequestEx.contentUTF8());
            var ersErrorCode = getFirstErrorCodeAsEnum(validationErrors, ErsErrorCode.class);
            handleValidationError(ersRequest, ersErrorCode);
            ersRequest.setDetails(badRequestEx.getMessage());
            ersRequestRepository.save(ersRequest);
            log.info("Ers request [{}] has been updated with status [{}]", ersRequest.getRequestId(),
                    ersRequest.getResponseStatus());
            return ersErrorCode;
        } catch (Exception ex) {
            log.error("Got error while handling bad request with status [{}] for request id [{}]",
                    badRequestEx.status(), ersRequest.getRequestId());
            handleErrorRequest(ersRequest, ErsResponseStatus.ERROR, ex.getMessage());
        }
        return null;
    }

    private void handleValidationError(ErsRequest ersRequest, ErsErrorCode ersErrorCode) {
        if (ersErrorCode == null) {
            log.warn("Got unknown ers error code for request id [{}]. Set ERROR response status",
                    ersRequest.getRequestId());
            ersRequest.setResponseStatus(ErsResponseStatus.ERROR);
        } else {
            ersRequest.setResponseStatus(ersResponseStatusMapper.map(ersErrorCode));
        }
    }

    private boolean notFatal(Exception ex) {
        return Stream.of(notFatalExceptions)
                .anyMatch(notFatalException -> notFatalException.isAssignableFrom(ex.getClass()));
    }
}
