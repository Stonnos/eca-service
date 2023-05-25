package com.ecaservice.audit.exception;

import com.ecaservice.audit.error.AuditErrorCode;
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
        super(AuditErrorCode.DUPLICATE_EVENT_ID, String.format("Event id = [%s] is already exists!", eventId));
    }
}
