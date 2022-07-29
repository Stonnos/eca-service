package com.ecaservice.web.push.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Invalid message type exception.
 *
 * @author Roman Batygin
 */
public class InvalidMessageTypeException extends ValidationErrorException {

    private static final String INVALID_MESSAGE_TYPE = "InvalidMessageType";

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public InvalidMessageTypeException(String message) {
        super(INVALID_MESSAGE_TYPE, message);
    }
}
