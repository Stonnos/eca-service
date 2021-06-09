package com.ecaservice.core.audit.exception;

/**
 * Audit entity not found exception.
 *
 * @author Roman Batygin
 */
public class AuditEntityNotFoundException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public AuditEntityNotFoundException(String message) {
        super(message);
    }
}
