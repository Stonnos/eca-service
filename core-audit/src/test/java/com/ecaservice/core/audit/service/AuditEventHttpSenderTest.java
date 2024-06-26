package com.ecaservice.core.audit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.ecaservice.core.audit.TestHelperUtils.createAuditEventRequest;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AuditEventHttpSender} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(AuditEventHttpSender.class)
class AuditEventHttpSenderTest {

    @Autowired
    private AuditEventHttpSender auditEventHttpSender;

    @MockBean
    private AuditEventClient auditEventClient;

    @Test
    void testSendAuditEvent() {
        var auditEventRequest = createAuditEventRequest();
        auditEventHttpSender.sendAuditEvent(auditEventRequest);
        verify(auditEventClient, atLeastOnce()).sendEvent(auditEventRequest);
    }
}
