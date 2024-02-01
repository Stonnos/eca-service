package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;
import com.ecaservice.user.profile.options.dto.UserNotificationEventType;

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
    public NotificationEventNotFoundException(List<UserNotificationEventType> eventTypes) {
        super(EcaOauthErrorCode.NOTIFICATION_EVENT_NOT_FOUND,
                String.format("Notification events [%s] not found", eventTypes));
    }
}
