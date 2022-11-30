package com.ecaservice.core.audit.service.impl;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.core.audit.AbstractJpaTest;
import com.ecaservice.core.audit.entity.AuditEventTemplateEntity;
import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.mapping.AuditMapperImpl;
import com.ecaservice.core.audit.model.AuditContextParams;
import com.ecaservice.core.audit.repository.AuditCodeRepository;
import com.ecaservice.core.audit.repository.AuditEventTemplateRepository;
import com.ecaservice.core.audit.repository.AuditGroupRepository;
import com.ecaservice.core.audit.service.AuditEventSender;
import com.ecaservice.core.audit.service.store.DatabaseAuditEventTemplateStore;
import com.ecaservice.core.audit.service.template.AuditTemplateProcessorService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.core.audit.TestHelperUtils.createAuditEventTemplateEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SimpleAuditEventService} class.
 *
 * @author Roman Batygin
 */
@Import({SimpleAuditEventService.class, AuditMapperImpl.class, DatabaseAuditEventTemplateStore.class})
class SimpleAuditEventServiceTest extends AbstractJpaTest {

    private static final String MESSAGE = "Message";
    private static final String INITIATOR = "user";

    @Inject
    private SimpleAuditEventService auditEventService;

    @Inject
    private AuditEventTemplateRepository auditEventTemplateRepository;
    @Inject
    private AuditCodeRepository auditCodeRepository;
    @Inject
    private AuditGroupRepository auditGroupRepository;

    @MockBean
    private AuditEventSender auditEventSender;
    @MockBean
    private AuditTemplateProcessorService auditTemplateProcessorService;

    @Captor
    private ArgumentCaptor<AuditEventRequest> auditEventRequestArgumentCaptor;

    private AuditEventTemplateEntity auditEventTemplateEntity;

    @Override
    public void init() {
        auditEventTemplateEntity = createAuditEventTemplateEntity();
        auditGroupRepository.save(auditEventTemplateEntity.getAuditCode().getAuditGroup());
        auditCodeRepository.save(auditEventTemplateEntity.getAuditCode());
        auditEventTemplateRepository.save(auditEventTemplateEntity);
    }

    @Override
    public void deleteAll() {
        auditEventTemplateRepository.deleteAll();
        auditCodeRepository.deleteAll();
        auditGroupRepository.deleteAll();
    }

    @Test
    void testAudit() {
        String auditCode = auditEventTemplateEntity.getAuditCode().getId();
        var contextParams = new AuditContextParams();
        when(auditTemplateProcessorService.process(auditCode, auditEventTemplateEntity.getEventType(),
                contextParams)).thenReturn(MESSAGE);
        var auditEvent = new AuditEvent(this, auditCode, auditEventTemplateEntity.getEventType(),
                UUID.randomUUID().toString(), INITIATOR, contextParams);
        auditEventService.audit(auditEvent);
        verify(auditEventSender, atLeastOnce()).sendAuditEvent(auditEventRequestArgumentCaptor.capture());
        var auditEventRequest = auditEventRequestArgumentCaptor.getValue();
        assertThat(auditEventRequest).isNotNull();
        assertThat(auditEventRequest.getEventId()).isNotNull();
        assertThat(auditEventRequest.getEventDate()).isNotNull();
        assertThat(auditEventRequest.getInitiator()).isEqualTo(auditEvent.getInitiator());
        assertThat(auditEventRequest.getCorrelationId()).isEqualTo(auditEvent.getCorrelationId());
        assertThat(auditEventRequest.getMessage()).isEqualTo(MESSAGE);
    }
}
