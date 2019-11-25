package com.ecaservice.exception.notification;

/**
 * Invalid request params exception.
 *
 * @author Roman Batygin
 */
public class InvalidRequestParamsException extends NotificationServiceException {
    /**
     * Creates notification exception.
     *
     * @param message error message
     */
    public InvalidRequestParamsException(String message) {
        super(message);
    }
}
