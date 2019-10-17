package com.ecaservice.exception.notification;

/**
 * Error status exception.
 *
 * @author Roman Batygin
 */
public class ErrorStatusException extends NotificationServiceException {

    /**
     * Creates error status exception.
     *
     * @param message error message
     */
    public ErrorStatusException(String message) {
        super(message);
    }
}
