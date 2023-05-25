package com.ecaservice.common.web.exception;

import com.ecaservice.common.web.error.CommonErrorCode;

/**
 * Invalid operation exception.
 *
 * @author Roman Batygin
 */
public class InvalidOperationException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public InvalidOperationException(String message) {
        super(CommonErrorCode.INVALID_OPERATION, message);
    }
}
