package com.ecaservice.audit.mapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.audit.TestHelperUtils.createAuditEventRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AuditLogMapper} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(AuditLogMapperImpl.class)
class AuditLogMapperTest {

    @Inject
    private AuditLogMapper auditLogMapper;

    @Test
    void testMapAuditEventRequest() {
        var auditEventRequest = createAuditEventRequest();
        var auditLog = auditLogMapper.map(auditEventRequest);
        assertThat(auditLog).isNotNull();
        assertThat(auditLog.getEventId()).isEqualTo(auditEventRequest.getEventId());
        assertThat(auditLog.getEventType()).isEqualTo(auditEventRequest.getEventType());
        assertThat(auditLog.getEventDate()).isEqualTo(auditEventRequest.getEventDate());
        assertThat(auditLog.getMessage()).isEqualTo(auditEventRequest.getMessage());
        assertThat(auditLog.getCode()).isEqualTo(auditEventRequest.getCode());
        assertThat(auditLog.getCodeTitle()).isEqualTo(auditEventRequest.getCodeTitle());
        assertThat(auditLog.getGroupCode()).isEqualTo(auditEventRequest.getGroupCode());
        assertThat(auditLog.getGroupTitle()).isEqualTo(auditEventRequest.getGroupTitle());
    }
}
