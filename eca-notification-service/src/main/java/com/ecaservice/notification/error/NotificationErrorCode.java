package com.ecaservice.notification.error;

import com.ecaservice.common.error.model.ErrorDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Error code.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorDetails {

    /**
     * Invalid notifications ids
     */
    INVALID_NOTIFICATIONS_IDS("InvalidNotificationsIds"),

    /**
     * Duplicate notification event to update
     */
    DUPLICATE_NOTIFICATION_EVENT_TO_UPDATE("DuplicateNotificationEventToUpdate"),

    /**
     * Notification event not found
     */
    NOTIFICATION_EVENT_NOT_FOUND("NotificationEventNotFound"),

    /**
     * Duplicate request id
     */
    DUPLICATE_REQUEST_ID("DuplicateRequestId");

    /**
     * Error code
     */
    private final String code;
}
