package com.ecaservice.web.push.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.web.push.entity.NotificationEventType;
import com.ecaservice.web.push.error.WebPushErrorCode;

/**
 * Exception throws in case if duplicate notification event to update is present in request body.
 *
 * @author Roman Batygin
 */
public class DuplicateNotificationEventToUpdateException extends ValidationErrorException {

    /**
     * Constructor with parameters.
     *
     * @param eventType - notification event type
     */
    public DuplicateNotificationEventToUpdateException(NotificationEventType eventType) {
        super(WebPushErrorCode.DUPLICATE_NOTIFICATION_EVENT_TO_UPDATE,
                String.format("Duplicate notification event [%s] to update has been found in request body", eventType));
    }
}
