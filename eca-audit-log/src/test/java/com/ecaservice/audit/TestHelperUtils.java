package com.ecaservice.audit;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.dto.EventType;
import com.ecaservice.audit.entity.AuditLogEntity;
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

    private static final String INITIATOR = "user";
    private static final String CODE = "CODE";
    private static final String CODE_TITLE = "code title";
    private static final String GROUP = "GROUP";
    private static final String GROUP_TITLE = "group title";
    private static final String MESSAGE = "audit message";

    /**
     * Creates audit event request.
     *
     * @return audit event request
     */
    public static AuditEventRequest createAuditEventRequest() {
        var auditEventRequest = new AuditEventRequest();
        auditEventRequest.setEventId(UUID.randomUUID().toString());
        auditEventRequest.setEventType(EventType.SUCCESS);
        auditEventRequest.setInitiator(INITIATOR);
        auditEventRequest.setCode(CODE);
        auditEventRequest.setCodeTitle(CODE_TITLE);
        auditEventRequest.setGroupCode(GROUP);
        auditEventRequest.setGroupTitle(GROUP_TITLE);
        auditEventRequest.setMessage(MESSAGE);
        auditEventRequest.setEventDate(LocalDateTime.now());
        return auditEventRequest;
    }

    /**
     * Creates audit event request.
     *
     * @param groupCode - group code
     * @param code      - audit code
     * @return audit event request
     */
    public static AuditLogEntity createAuditLog(String groupCode, String code) {
        var auditLogEntity = new AuditLogEntity();
        auditLogEntity.setEventId(UUID.randomUUID().toString());
        auditLogEntity.setEventType(EventType.SUCCESS);
        auditLogEntity.setInitiator(INITIATOR);
        auditLogEntity.setCode(code);
        auditLogEntity.setCodeTitle(CODE_TITLE);
        auditLogEntity.setGroupCode(groupCode);
        auditLogEntity.setGroupTitle(GROUP_TITLE);
        auditLogEntity.setMessage(MESSAGE);
        auditLogEntity.setEventDate(LocalDateTime.now());
        return auditLogEntity;
    }

    /**
     * Creates audit log entity.
     *
     * @return audit log entity
     */
    public static AuditLogEntity createAuditLog() {
        return createAuditLog(GROUP, CODE);
    }
}
