package com.ecaservice.auto.test.exception;

import com.ecaservice.auto.test.error.ErrorCode;
import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Unsupported feature exception.
 *
 * @author Roman Batygin
 */
public class UnsupportedFeatureException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public UnsupportedFeatureException(String message) {
        super(ErrorCode.UNSUPPORTED_FEATURE.name(), message);
    }
}
