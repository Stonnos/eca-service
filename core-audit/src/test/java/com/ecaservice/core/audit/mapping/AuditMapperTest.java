package com.ecaservice.core.audit.mapping;

import com.ecaservice.core.audit.entity.EventStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.core.audit.TestHelperUtils.createAuditCodeEntity;
import static com.ecaservice.core.audit.TestHelperUtils.createAuditEventRequest;
import static com.ecaservice.core.audit.TestHelperUtils.createAuditEventRequestEntity;
import static com.ecaservice.core.audit.TestHelperUtils.createAuditEventTemplateEntity;
import static com.ecaservice.core.audit.TestHelperUtils.createAuditEventTemplateModel;
import static com.ecaservice.core.audit.TestHelperUtils.createAuditGroupEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AuditMapper} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(AuditMapperImpl.class)
class AuditMapperTest {

    @Inject
    private AuditMapper auditMapper;

    @Test
    void testMapAuditGroupEntityToModel() {
        var auditGroupEntity = createAuditGroupEntity();
        var auditGroupModel = auditMapper.map(auditGroupEntity);
        assertThat(auditGroupModel).isNotNull();
        assertThat(auditGroupModel.getGroupCode()).isEqualTo(auditGroupEntity.getId());
        assertThat(auditGroupModel.getTitle()).isEqualTo(auditGroupEntity.getTitle());
    }

    @Test
    void testMapAuditCodeEntityToModel() {
        var auditCodeEntity = createAuditCodeEntity();
        var auditCodeModel = auditMapper.map(auditCodeEntity);
        assertThat(auditCodeModel).isNotNull();
        assertThat(auditCodeModel.getCode()).isEqualTo(auditCodeEntity.getId());
        assertThat(auditCodeModel.getTitle()).isEqualTo(auditCodeEntity.getTitle());
        assertThat(auditCodeModel.isEnabled()).isEqualTo(auditCodeEntity.isEnabled());
        assertThat(auditCodeModel.getAuditGroup()).isNotNull();
    }

    @Test
    void testMapAuditEventTemplateEntityToModel() {
        var auditEventTemplateEntity = createAuditEventTemplateEntity();
        var auditEventTemplateModel = auditMapper.map(auditEventTemplateEntity);
        assertThat(auditEventTemplateModel).isNotNull();
        assertThat(auditEventTemplateModel.getEventType()).isEqualTo(auditEventTemplateEntity.getEventType());
        assertThat(auditEventTemplateModel.getMessageTemplate()).isEqualTo(
                auditEventTemplateEntity.getMessageTemplate());
        assertThat(auditEventTemplateModel.getAuditCode()).isNotNull();
    }

    @Test
    void testMapAuditEventTemplateModelToAuditEventRequest() {
        var auditEventTemplateModel = createAuditEventTemplateModel();
        var auditEventRequest = auditMapper.map(auditEventTemplateModel);
        assertThat(auditEventRequest).isNotNull();
        assertThat(auditEventRequest.getCode()).isEqualTo(auditEventTemplateModel.getAuditCode().getCode());
        assertThat(auditEventRequest.getCodeTitle()).isEqualTo(auditEventTemplateModel.getAuditCode().getTitle());
        assertThat(auditEventRequest.getGroupCode()).isEqualTo(
                auditEventTemplateModel.getAuditCode().getAuditGroup().getGroupCode());
        assertThat(auditEventRequest.getGroupTitle()).isEqualTo(
                auditEventTemplateModel.getAuditCode().getAuditGroup().getTitle());
        assertThat(auditEventRequest.getEventType()).isEqualTo(auditEventTemplateModel.getEventType());
    }

    @Test
    void testMapAuditEventRequest() {
        var auditEventRequest = createAuditEventRequest();
        var auditEventRequestEntity = auditMapper.map(auditEventRequest);
        assertThat(auditEventRequestEntity).isNotNull();
        assertThat(auditEventRequestEntity.getEventId()).isEqualTo(auditEventRequest.getEventId());
        assertThat(auditEventRequestEntity.getEventDate()).isEqualTo(auditEventRequest.getEventDate());
        assertThat(auditEventRequestEntity.getEventType()).isEqualTo(auditEventRequest.getEventType());
        assertThat(auditEventRequestEntity.getGroupCode()).isEqualTo(auditEventRequest.getGroupCode());
        assertThat(auditEventRequestEntity.getGroupTitle()).isEqualTo(auditEventRequest.getGroupTitle());
        assertThat(auditEventRequestEntity.getCode()).isEqualTo(auditEventRequest.getCode());
        assertThat(auditEventRequestEntity.getCodeTitle()).isEqualTo(auditEventRequest.getCodeTitle());
        assertThat(auditEventRequestEntity.getInitiator()).isEqualTo(auditEventRequest.getInitiator());
        assertThat(auditEventRequestEntity.getMessage()).isEqualTo(auditEventRequest.getMessage());
    }

    @Test
    void testMapAuditEventRequestEntity() {
        var auditEventRequestEntity = createAuditEventRequestEntity(EventStatus.SENT);
        var auditEventRequest = auditMapper.map(auditEventRequestEntity);
        assertThat(auditEventRequest).isNotNull();
        assertThat(auditEventRequest.getEventId()).isEqualTo(auditEventRequestEntity.getEventId());
        assertThat(auditEventRequest.getEventDate()).isEqualTo(auditEventRequestEntity.getEventDate());
        assertThat(auditEventRequest.getEventType()).isEqualTo(auditEventRequestEntity.getEventType());
        assertThat(auditEventRequest.getGroupCode()).isEqualTo(auditEventRequestEntity.getGroupCode());
        assertThat(auditEventRequest.getGroupTitle()).isEqualTo(auditEventRequestEntity.getGroupTitle());
        assertThat(auditEventRequest.getCode()).isEqualTo(auditEventRequestEntity.getCode());
        assertThat(auditEventRequest.getCodeTitle()).isEqualTo(auditEventRequestEntity.getCodeTitle());
        assertThat(auditEventRequest.getInitiator()).isEqualTo(auditEventRequestEntity.getInitiator());
        assertThat(auditEventRequest.getMessage()).isEqualTo(auditEventRequestEntity.getMessage());
    }
}
