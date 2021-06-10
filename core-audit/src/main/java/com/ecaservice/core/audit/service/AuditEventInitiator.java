package com.ecaservice.core.audit.service;

/**
 * Interface to manage with audit event initiator.
 *
 * @author Roman Batygin
 */
public interface AuditEventInitiator {

    /**
     * Gets audit event initiator.
     *
     * @return audit event initiator
     */
    String getInitiator();
}
