package com.ecaservice.server.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Instances validation exception class.
 *
 * @author Roman Batygin
 */
public class InstancesValidationException extends ValidationErrorException {

    /**
     * Creates exception.
     *
     * @param errorCode - error code
     * @param message   - error message
     */
    public InstancesValidationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
