package com.ecaservice.server.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.server.error.EsErrorCode;

/**
 * Classify instance invalid request exception.
 *
 * @author Roman Batygin
 */
public class ClassifyInstanceInvalidRequestException extends ValidationErrorException {

    /**
     * Creates exception.
     *
     * @param errorMessage - error message
     */
    public ClassifyInstanceInvalidRequestException(String errorMessage) {
        super(EsErrorCode.CLASSIFY_INSTANCE_INVALID_REQUEST, errorMessage);
    }
}
