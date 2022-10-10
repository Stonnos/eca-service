package com.ecaservice.web.push.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;

import java.util.List;

/**
 * Invalid notifications exception class.
 *
 * @author Roman Batygin
 */
public class InvalidNotificationsException extends ValidationErrorException {

    private static final String ERROR_CODE = "InvalidNotifications";

    /**
     * Creates invalid notifications exception.
     *
     * @param invalidIds - invalid notifications ids
     */
    public InvalidNotificationsException(List<Long> invalidIds) {
        super(ERROR_CODE, String.format("Got invalid notifications ids %s", invalidIds));
    }
}
