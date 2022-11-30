package com.ecaservice.core.audit.event.handler;

import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.model.AuditContextParams;
import com.ecaservice.core.audit.service.impl.SimpleAuditEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AuditEventListener} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class AuditEventListenerTest {

    private static final String AUDIT_CODE = "code";
    private static final String INITIATOR = "user";

    @Mock
    private SimpleAuditEventService auditEventService;

    @InjectMocks
    private AuditEventListener auditEventListener;

    @Test
    void testHandleAuditEvent() {
        var contextParams = new AuditContextParams();
        var auditEvent = new AuditEvent(this, AUDIT_CODE, EventType.SUCCESS, UUID.randomUUID().toString(),
                INITIATOR, contextParams);
        auditEventListener.handleAuditEvent(auditEvent);
        verify(auditEventService, atLeastOnce()).audit(auditEvent);
    }
}
