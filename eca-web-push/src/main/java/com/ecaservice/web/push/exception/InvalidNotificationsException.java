package com.ecaservice.web.push.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.web.push.error.ErrorCode;

import java.util.List;

/**
 * Invalid notifications exception class.
 *
 * @author Roman Batygin
 */
public class InvalidNotificationsException extends ValidationErrorException {

    /**
     * Creates invalid notifications exception.
     *
     * @param invalidIds - invalid notifications ids
     */
    public InvalidNotificationsException(List<Long> invalidIds) {
        super(ErrorCode.INVALID_NOTIFICATIONS.name(), String.format("Got invalid notifications ids %s", invalidIds));
    }
}
