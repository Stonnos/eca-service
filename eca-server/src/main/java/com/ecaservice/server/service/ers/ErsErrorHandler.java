package com.ecaservice.server.service.ers;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.ers.dto.ErsErrorCode;
import com.ecaservice.server.mapping.ErsResponseStatusMapper;
import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.repository.ErsRequestRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ecaservice.common.web.util.ValidationErrorHelper.getFirstError;
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
            var ersErrorCode = getErsErrorCode(validationErrors);
            handleValidationError(ersRequest, ersErrorCode);
            ersRequest.setDetails(badRequestEx.getMessage());
            ersRequestRepository.save(ersRequest);
            return ersErrorCode;
        } catch (Exception ex) {
            log.error("Got error while handling bad request with status [{}] for request id [{}]",
                    badRequestEx.status(), ersRequest.getRequestId());
            handleErrorRequest(ersRequest, ErsResponseStatus.ERROR, ex.getMessage());
        }
        return null;
    }

    private ErsErrorCode getErsErrorCode(List<ValidationErrorDto> validationErrors) {
        var errorCodes = Stream.of(ErsErrorCode.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        var validationError = getFirstError(errorCodes, validationErrors);
        return validationError
                .map(validationErrorDto -> ErsErrorCode.valueOf(validationErrorDto.getCode()))
                .orElse(null);
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
}
