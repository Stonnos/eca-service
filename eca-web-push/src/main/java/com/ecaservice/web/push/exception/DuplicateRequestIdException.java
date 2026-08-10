package com.ecaservice.web.push.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.web.push.error.WebPushErrorCode;

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
        super(WebPushErrorCode.DUPLICATE_REQUEST_ID,
                String.format("Email with request id = [%s] is already exists!", requestId));
    }
}
