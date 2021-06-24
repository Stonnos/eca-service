package com.ecaservice.core.audit.exception;

/**
 * Audit template processing exception.
 *
 * @author Roman Batygin
 */
public class AuditTemplateProcessingException extends RuntimeException {

    /**
     * Creates audit template processing exception.
     *
     * @param message - message
     */
    public AuditTemplateProcessingException(String message) {
        super(message);
    }
}
