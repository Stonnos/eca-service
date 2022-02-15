package com.ecaservice.core.audit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.core.audit.TestHelperUtils.createAuditEventRequest;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AuditEventSender} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(AuditEventSender.class)
class AuditEventSenderTest {

    @Inject
    private AuditEventSender auditEventSender;

    @MockBean
    private AuditEventClient auditEventClient;

    @Test
    void testSendAuditEvent() {
        var auditEventRequest = createAuditEventRequest();
        auditEventSender.sendAuditEvent(auditEventRequest);
        verify(auditEventClient, atLeastOnce()).sendEvent(auditEventRequest);
    }
}
