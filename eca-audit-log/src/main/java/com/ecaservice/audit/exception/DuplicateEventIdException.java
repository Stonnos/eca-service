package com.ecaservice.audit.exception;

import com.ecaservice.audit.error.ErrorCode;
import com.ecaservice.common.web.exception.ValidationErrorException;

/**
 * Duplicate event id exception class.
 *
 * @author Roman Batygin
 */
public class DuplicateEventIdException extends ValidationErrorException {

    /**
     * Creates duplicate event id exception.
     *
     * @param eventId - event id
     */
    public DuplicateEventIdException(String eventId) {
        super(ErrorCode.DUPLICATE_EVENT_ID.name(), String.format("Event id = [%s] is already exists!", eventId));
    }
}
