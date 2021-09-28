package com.ecaservice.ers.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.ers.dto.ErsErrorCode;

/**
 * Duplicate request id exception class.
 *
 * @author Roman Batygin
 */
public class DuplicateRequestIdException extends ValidationErrorException {

    /**
     * Creates duplicate request id exception.
     *
     * @param requestId - request id
     */
    public DuplicateRequestIdException(String requestId) {
        super(ErsErrorCode.DUPLICATE_REQUEST_ID.name(),
                String.format("Evaluation results with request id = [%s] is already exists!", requestId));
    }
}
