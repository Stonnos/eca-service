package com.ecaservice.core.audit.service.impl;

import com.ecaservice.core.audit.service.AuditEventInitiator;
import org.springframework.stereotype.Service;

/**
 * Event initiator default implementation.
 *
 * @author Roman Batygin
 */
@Service
public class DefaultAuditEventInitiator implements AuditEventInitiator {

    @Override
    public String getInitiator() {
        return "system";
    }
}
