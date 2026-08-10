package com.ecaservice.web.push.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.web.push.entity.NotificationEventType;
import com.ecaservice.web.push.error.WebPushErrorCode;

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
        super(WebPushErrorCode.NOTIFICATION_EVENT_NOT_FOUND,
                String.format("Notification events [%s] not found", eventTypes));
    }
}
