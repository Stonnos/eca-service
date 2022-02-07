package com.ecaservice.core.filter.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Invalid value format exception.
 *
 * @author Roman Batygin
 */
public class InvalidValueFormatException extends ValidationErrorException {

    private static final String ERROR_CODE = "InvalidValueFormat";

    /**
     * Constructor with parameters.
     *
     * @param errorMessage - error message
     */
    public InvalidValueFormatException(String errorMessage) {
        super(ERROR_CODE, errorMessage);
    }
}
