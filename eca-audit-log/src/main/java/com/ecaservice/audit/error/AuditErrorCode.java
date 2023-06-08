package com.ecaservice.audit.error;

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
public enum AuditErrorCode implements ErrorDetails {

    /**
     * Duplicate event id
     */
    DUPLICATE_EVENT_ID("DuplicateEventId");

    /**
     * Error code
     */
    private final String code;
}
