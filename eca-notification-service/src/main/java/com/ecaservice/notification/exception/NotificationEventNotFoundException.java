package com.ecaservice.notification.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.notification.entity.NotificationEventType;
import com.ecaservice.notification.error.NotificationErrorCode;

import java.util.List;

/**
 * Exception throws in case if notification event not found.
 *
 * @author Roman Batygin
 */
public class NotificationEventNotFoundException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     *
     * @param eventTypes - notification event type
     */
    public NotificationEventNotFoundException(List<NotificationEventType> eventTypes) {
        super(NotificationErrorCode.NOTIFICATION_EVENT_NOT_FOUND,
                String.format("Notification events [%s] not found", eventTypes));
    }
}
