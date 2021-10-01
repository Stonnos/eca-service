package com.ecaservice.core.audit.service;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.core.audit.AbstractJpaTest;
import com.ecaservice.core.audit.config.AuditProperties;
import com.ecaservice.core.audit.entity.AuditEventRequestEntity;
import com.ecaservice.core.audit.entity.EventStatus;
import com.ecaservice.core.audit.mapping.AuditMapperImpl;
import com.ecaservice.core.audit.repository.AuditEventRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Arrays;

import static com.ecaservice.core.audit.TestHelperUtils.createAuditEventRequestEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AuditEventRedeliveryService} class.
 *
 * @author Roman Batygin
 */
@Import({AuditEventRedeliveryService.class, AuditMapperImpl.class, AuditProperties.class})
class AuditEventRedeliveryServiceTest extends AbstractJpaTest {

    @Inject
    private AuditEventRequestRepository auditEventRequestRepository;
    @Inject
    private AuditEventRedeliveryService auditEventRedeliveryService;

    @MockBean
    private AuditEventSender auditEventSender;

    @Override
    public void deleteAll() {
        auditEventRequestRepository.deleteAll();
    }

    @Test
    void testProcessNotSentEvents() {
        var first = createAuditEventRequestEntity(EventStatus.SENT);
        var second = createAuditEventRequestEntity(EventStatus.NOT_SENT);
        auditEventRequestRepository.saveAll(Arrays.asList(first, second));
        auditEventRedeliveryService.processNotSentEvents();
        verify(auditEventSender, atLeastOnce()).sendAuditEvent(any(AuditEventRequest.class),
                any(AuditEventRequestEntity.class));
    }
}
