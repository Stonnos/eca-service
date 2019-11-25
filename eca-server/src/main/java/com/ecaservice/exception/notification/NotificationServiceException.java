package com.ecaservice.exception.notification;

import com.ecaservice.exception.EcaServiceException;

/**
 * Notification service exception class.
 *
 * @author Roman Batygin
 */
public class NotificationServiceException extends EcaServiceException {

    /**
     * Creates notification exception.
     *
     * @param message error message
     */
    public NotificationServiceException(String message) {
        super(message);
    }
}
