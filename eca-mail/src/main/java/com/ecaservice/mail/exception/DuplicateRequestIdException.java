package com.ecaservice.mail.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.mail.error.ErrorCode;

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
        super(ErrorCode.DUPLICATE_REQUEST_ID.name(),
                String.format("Email with request id = [%s] is already exists!", requestId));
    }
}
