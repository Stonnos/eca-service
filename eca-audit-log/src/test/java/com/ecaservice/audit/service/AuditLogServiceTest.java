package com.ecaservice.audit.service;

import com.ecaservice.audit.AbstractJpaTest;
import com.ecaservice.audit.config.EcaAuditLogConfig;
import com.ecaservice.audit.mapping.AuditLogMapperImpl;
import com.ecaservice.audit.repository.AuditLogRepository;
import com.ecaservice.core.filter.annotation.EnableFilters;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static com.ecaservice.audit.TestHelperUtils.createAuditEventRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AuditLogService} class.
 *
 * @author Roman Batygin
 */
@EnableFilters
@Import({AuditLogMapperImpl.class, AuditLogService.class, EcaAuditLogConfig.class})
class AuditLogServiceTest extends AbstractJpaTest {

    @Inject
    private AuditLogService auditLogService;

    @Inject
    private AuditLogRepository auditLogRepository;

    @Override
    public void deleteAll() {
        auditLogRepository.deleteAll();
    }

    @Test
    void testSaveAuditLog() {
        var auditEventRequest = createAuditEventRequest();
        var auditLog = auditLogService.save(auditEventRequest);
        assertThat(auditLog).isNotNull();
        var actual = auditLogRepository.findById(auditLog.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getEventId()).isEqualTo(auditEventRequest.getEventId());
    }
}
