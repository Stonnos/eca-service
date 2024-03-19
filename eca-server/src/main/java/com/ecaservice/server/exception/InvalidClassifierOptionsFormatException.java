package com.ecaservice.server.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.server.error.EsErrorCode;

/**
 * Invalid classifier options format exception.
 *
 * @author Roman Batygin
 */
public class InvalidClassifierOptionsFormatException extends ValidationErrorException {

    /**
     * Creates exception.
     */
    public InvalidClassifierOptionsFormatException() {
        super(EsErrorCode.INVALID_CLASSIFIER_OPTIONS_FORMAT, "Invalid classifier options format");
    }
}
