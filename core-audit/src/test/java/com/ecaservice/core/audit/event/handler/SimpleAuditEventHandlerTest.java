package com.ecaservice.core.audit.event.handler;

import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.model.AuditContextParams;
import com.ecaservice.core.audit.service.AuditEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link SimpleAuditEventHandler} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class SimpleAuditEventHandlerTest {

    private static final String AUDIT_CODE = "code";
    private static final String INITIATOR = "user";

    @Mock
    private AuditEventService auditEventService;

    @InjectMocks
    private SimpleAuditEventHandler simpleAuditEventHandler;

    @Test
    void testHandleAuditEvent() {
        var contextParams = new AuditContextParams();
        var auditEvent = new AuditEvent(this, AUDIT_CODE, EventType.SUCCESS, UUID.randomUUID().toString(),
                INITIATOR, contextParams);
        simpleAuditEventHandler.handleAuditEvent(auditEvent);
        verify(auditEventService, atLeastOnce()).audit(auditEvent);
    }
}
