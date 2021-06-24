package com.ecaservice.core.audit.exception;

/**
 * Audit data not found exception.
 *
 * @author Roman Batygin
 */
public class AuditDataNotFoundException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public AuditDataNotFoundException(String message) {
        super(message);
    }
}
