package com.ecaservice.server.exception;

import com.ecaservice.common.error.model.ErrorDetails;
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
     * @param errorDetails - error details
     * @param message   - error message
     */
    public InstancesValidationException(ErrorDetails errorDetails, String message) {
        super(errorDetails, message);
    }
}
