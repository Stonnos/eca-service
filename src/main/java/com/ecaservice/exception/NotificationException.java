package com.ecaservice.exception;

/**
 * Notification exception class.
 *
 * @author Roman Batygin
 */
public class NotificationException extends EcaServiceException {

    /**
     * Creates notification exception.
     *
     * @param message error message
     */
    public NotificationException(String message) {
        super(message);
    }
}
