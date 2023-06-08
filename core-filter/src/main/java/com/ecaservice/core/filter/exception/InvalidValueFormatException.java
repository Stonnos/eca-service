package com.ecaservice.core.filter.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.core.filter.error.CoreFilterErrorCode;

/**
 * Invalid value format exception.
 *
 * @author Roman Batygin
 */
public class InvalidValueFormatException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     *
     * @param errorMessage - error message
     * @param fieldName    - field name
     */
    public InvalidValueFormatException(String errorMessage, String fieldName) {
        super(CoreFilterErrorCode.INVALID_VALUE_FORMAT, errorMessage, fieldName);
    }
}
