package com.ecaservice.notification.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.notification.error.NotificationErrorCode;

import java.util.List;

/**
 * Invalid notifications exception class.
 *
 * @author Roman Batygin
 */
public class InvalidNotificationsIdsException extends ValidationErrorException {

    /**
     * Creates invalid notifications exception.
     *
     * @param invalidIds - invalid notifications ids
     */
    public InvalidNotificationsIdsException(List<Long> invalidIds) {
        super(NotificationErrorCode.INVALID_NOTIFICATIONS_IDS,
                String.format("Got invalid notifications ids %s", invalidIds));
    }
}
