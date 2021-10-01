package com.ecaservice.core.audit;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.entity.AuditCodeEntity;
import com.ecaservice.core.audit.entity.AuditEventRequestEntity;
import com.ecaservice.core.audit.entity.AuditEventTemplateEntity;
import com.ecaservice.core.audit.entity.AuditGroupEntity;
import com.ecaservice.core.audit.entity.EventStatus;
import com.ecaservice.core.audit.model.AuditCodeModel;
import com.ecaservice.core.audit.model.AuditEventTemplateModel;
import com.ecaservice.core.audit.model.AuditGroupModel;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String GROUP_TITLE = "Group title";
    private static final String GROUP = "GROUP";
    private static final String CODE_TITLE = "Code title";
    private static final String CODE = "code";
    private static final String MESSAGE_TEMPLATE = "Message";
    private static final long ID = 1L;
    public static final String MESSAGE = "message";
    public static final String INITIATOR = "initiator";

    /**
     * Creates audit group entity.
     *
     * @return audit group entity
     */
    public static AuditGroupEntity createAuditGroupEntity() {
        var auditGroupEntity = new AuditGroupEntity();
        auditGroupEntity.setId(GROUP);
        auditGroupEntity.setTitle(GROUP_TITLE);
        return auditGroupEntity;
    }

    /**
     * Creates audit code entity.
     *
     * @return audit code entity
     */
    public static AuditCodeEntity createAuditCodeEntity() {
        var auditCodeEntity = new AuditCodeEntity();
        auditCodeEntity.setId(CODE);
        auditCodeEntity.setTitle(CODE_TITLE);
        auditCodeEntity.setEnabled(true);
        auditCodeEntity.setAuditGroup(createAuditGroupEntity());
        return auditCodeEntity;
    }

    /**
     * Creates audit event template entity.
     *
     * @return audit event template entity
     */
    public static AuditEventTemplateEntity createAuditEventTemplateEntity() {
        var auditEventTemplateEntity = new AuditEventTemplateEntity();
        auditEventTemplateEntity.setId(ID);
        auditEventTemplateEntity.setEventType(EventType.SUCCESS);
        auditEventTemplateEntity.setAuditCode(createAuditCodeEntity());
        auditEventTemplateEntity.setMessageTemplate(MESSAGE_TEMPLATE);
        return auditEventTemplateEntity;
    }

    /**
     * Creates audit event template model.
     *
     * @return audit event template model
     */
    public static AuditEventTemplateModel createAuditEventTemplateModel() {
        var auditEventTemplateModel = new AuditEventTemplateModel();
        auditEventTemplateModel.setEventType(EventType.SUCCESS);
        auditEventTemplateModel.setMessageTemplate(MESSAGE_TEMPLATE);
        auditEventTemplateModel.setAuditCode(createAuditCodeModel());
        return auditEventTemplateModel;
    }

    /**
     * Creates audit code model.
     *
     * @return audit code model
     */
    public static AuditCodeModel createAuditCodeModel() {
        var auditCodeModel = new AuditCodeModel();
        auditCodeModel.setCode(CODE);
        auditCodeModel.setTitle(CODE_TITLE);
        auditCodeModel.setEnabled(true);
        auditCodeModel.setAuditGroup(createAuditGroupModel());
        return auditCodeModel;
    }

    /**
     * Creates audit group model.
     *
     * @return audit group model
     */
    public static AuditGroupModel createAuditGroupModel() {
        var auditGroupModel = new AuditGroupModel();
        auditGroupModel.setGroupCode(GROUP);
        auditGroupModel.setTitle(GROUP_TITLE);
        return auditGroupModel;
    }

    /**
     * Creates audit event request dto.
     *
     * @return audit event request dto
     */
    public static AuditEventRequest createAuditEventRequest() {
        var auditEventRequest = new AuditEventRequest();
        auditEventRequest.setEventId(UUID.randomUUID().toString());
        auditEventRequest.setMessage(MESSAGE);
        auditEventRequest.setGroupCode(GROUP);
        auditEventRequest.setGroupTitle(GROUP_TITLE);
        auditEventRequest.setCode(CODE);
        auditEventRequest.setGroupTitle(CODE_TITLE);
        auditEventRequest.setEventType(EventType.SUCCESS);
        auditEventRequest.setInitiator(INITIATOR);
        auditEventRequest.setEventDate(LocalDateTime.now());
        return auditEventRequest;
    }

    /**
     * Creates audit event request entity.
     *
     * @param eventStatus - event status
     * @return audit event request entity
     */
    public static AuditEventRequestEntity createAuditEventRequestEntity(EventStatus eventStatus) {
        var auditEventRequestEntity = new AuditEventRequestEntity();
        auditEventRequestEntity.setEventId(UUID.randomUUID().toString());
        auditEventRequestEntity.setMessage(MESSAGE);
        auditEventRequestEntity.setGroupCode(GROUP);
        auditEventRequestEntity.setGroupTitle(GROUP_TITLE);
        auditEventRequestEntity.setCode(CODE);
        auditEventRequestEntity.setGroupTitle(CODE_TITLE);
        auditEventRequestEntity.setEventType(EventType.SUCCESS);
        auditEventRequestEntity.setInitiator(INITIATOR);
        auditEventRequestEntity.setEventDate(LocalDateTime.now());
        auditEventRequestEntity.setEventStatus(eventStatus);
        return auditEventRequestEntity;
    }
}
