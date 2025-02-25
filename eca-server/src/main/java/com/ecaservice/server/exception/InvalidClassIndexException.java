package com.ecaservice.server.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.server.error.EsErrorCode;

/**
 * Invalid class index exception.
 *
 * @author Roman Batygin
 */
public class InvalidClassIndexException extends ValidationErrorException {

    /**
     * Creates exception.
     *
     * @param errorMessage - error message
     */
    public InvalidClassIndexException(String errorMessage) {
        super(EsErrorCode.INVALID_CLASS_INDEX, errorMessage);
    }
}
