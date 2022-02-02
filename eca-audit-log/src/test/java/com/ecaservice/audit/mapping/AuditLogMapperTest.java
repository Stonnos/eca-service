package com.ecaservice.audit.mapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.audit.TestHelperUtils.createAuditEventRequest;
import static com.ecaservice.audit.TestHelperUtils.createAuditLog;
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
        assertThat(auditLog.getCorrelationId()).isEqualTo(auditEventRequest.getCorrelationId());
        assertThat(auditLog.getEventType()).isEqualTo(auditEventRequest.getEventType());
        assertThat(auditLog.getEventDate()).isEqualTo(auditEventRequest.getEventDate());
        assertThat(auditLog.getMessage()).isEqualTo(auditEventRequest.getMessage());
        assertThat(auditLog.getCode()).isEqualTo(auditEventRequest.getCode());
        assertThat(auditLog.getCodeTitle()).isEqualTo(auditEventRequest.getCodeTitle());
        assertThat(auditLog.getGroupCode()).isEqualTo(auditEventRequest.getGroupCode());
        assertThat(auditLog.getGroupTitle()).isEqualTo(auditEventRequest.getGroupTitle());
    }

    @Test
    void testMapAuditLogToDto() {
        var auditLog = createAuditLog();
        var auditLogDto = auditLogMapper.map(auditLog);
        assertThat(auditLogDto).isNotNull();
        assertThat(auditLogDto.getEventId()).isEqualTo(auditLog.getEventId());
        assertThat(auditLogDto.getCorrelationId()).isEqualTo(auditLog.getCorrelationId());
        assertThat(auditLogDto.getEventDate()).isEqualTo(auditLog.getEventDate());
        assertThat(auditLogDto.getMessage()).isEqualTo(auditLog.getMessage());
        assertThat(auditLogDto.getCode()).isEqualTo(auditLog.getCode());
        assertThat(auditLogDto.getCodeTitle()).isEqualTo(auditLog.getCodeTitle());
        assertThat(auditLogDto.getGroupCode()).isEqualTo(auditLog.getGroupCode());
        assertThat(auditLogDto.getGroupTitle()).isEqualTo(auditLog.getGroupTitle());
    }

    @Test
    void testMapAuditLogToBean() {
        var auditLog = createAuditLog();
        var auditLogBean = auditLogMapper.mapToBean(auditLog);
        assertThat(auditLogBean).isNotNull();
        assertThat(auditLogBean.getEventId()).isEqualTo(auditLog.getEventId());
        assertThat(auditLogBean.getEventDate()).isNotNull();
        assertThat(auditLogBean.getMessage()).isEqualTo(auditLog.getMessage());
        assertThat(auditLogBean.getCode()).isEqualTo(auditLog.getCode());
        assertThat(auditLogBean.getCodeTitle()).isEqualTo(auditLog.getCodeTitle());
        assertThat(auditLogBean.getGroupCode()).isEqualTo(auditLog.getGroupCode());
        assertThat(auditLogBean.getGroupTitle()).isEqualTo(auditLog.getGroupTitle());
    }
}
