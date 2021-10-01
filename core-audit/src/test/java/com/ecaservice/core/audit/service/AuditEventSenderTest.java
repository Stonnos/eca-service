package com.ecaservice.core.audit.service;

import com.ecaservice.core.audit.AbstractJpaTest;
import com.ecaservice.core.audit.entity.EventStatus;
import com.ecaservice.core.audit.repository.AuditEventRequestRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static com.ecaservice.core.audit.TestHelperUtils.createAuditEventRequest;
import static com.ecaservice.core.audit.TestHelperUtils.createAuditEventRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

/**
 * Unit tests for {@link AuditEventSender} class.
 *
 * @author Roman Batygin
 */
@Import(AuditEventSender.class)
class AuditEventSenderTest extends AbstractJpaTest {

    @Inject
    private AuditEventRequestRepository auditEventRequestRepository;
    @Inject
    private AuditEventSender auditEventSender;

    @MockBean
    private AuditEventClient auditEventClient;

    @Override
    public void deleteAll() {
        auditEventRequestRepository.deleteAll();
    }

    @Test
    void testSuccessSent() {
        var auditEventRequest = createAuditEventRequest();
        var auditEventRequestEntity = createAuditEventRequestEntity(null);
        auditEventSender.sendAuditEvent(auditEventRequest, auditEventRequestEntity);
        var actual = auditEventRequestRepository.findById(auditEventRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getEventStatus()).isEqualTo(EventStatus.SENT);
    }

    @Test
    void testNotSent() {
        var auditEventRequest = createAuditEventRequest();
        var auditEventRequestEntity = createAuditEventRequestEntity(null);
        doThrow(FeignException.ServiceUnavailable.class).when(auditEventClient).sendEvent(auditEventRequest);
        auditEventSender.sendAuditEvent(auditEventRequest, auditEventRequestEntity);
        var actual = auditEventRequestRepository.findById(auditEventRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getEventStatus()).isEqualTo(EventStatus.NOT_SENT);
    }
}
