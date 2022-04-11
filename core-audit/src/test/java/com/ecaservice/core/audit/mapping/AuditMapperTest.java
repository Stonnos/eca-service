package com.ecaservice.core.audit.mapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.core.audit.TestHelperUtils.createAuditCodeEntity;
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
}
