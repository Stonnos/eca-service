package com.ecaservice.exception.notification;

/**
 * Unknown error status exception.
 *
 * @author Roman Batygin
 */
public class UnknownErrorStatusException extends NotificationServiceException {

    /**
     * Creates unknown error status exception.
     *
     * @param message error message
     */
    public UnknownErrorStatusException(String message) {
        super(message);
    }
}
