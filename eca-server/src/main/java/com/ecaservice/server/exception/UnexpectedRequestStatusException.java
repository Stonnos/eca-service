package com.ecaservice.server.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.server.error.EsErrorCode;

/**
 * Unexpected request exception.
 *
 * @author Roman Batygin
 */
public class UnexpectedRequestStatusException extends ValidationErrorException {

    /**
     * Creates exception.
     *
     * @param errorMessage - error message
     */
    public UnexpectedRequestStatusException(String errorMessage) {
        super(EsErrorCode.UNEXPECTED_REQUEST_STATUS, errorMessage);
    }
}
