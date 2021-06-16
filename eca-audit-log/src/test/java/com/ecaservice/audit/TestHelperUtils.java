package com.ecaservice.audit;

import com.ecaservice.audit.dto.AuditEventRequest;
import com.ecaservice.audit.dto.EventType;
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
        return createAuditEventRequest(GROUP, CODE);
    }

    /**
     * Creates audit event request.
     *
     * @param groupCode - group code
     * @param code      - audit code
     * @return audit event request
     */
    public static AuditEventRequest createAuditEventRequest(String groupCode, String code) {
        var auditEventRequest = new AuditEventRequest();
        auditEventRequest.setEventId(UUID.randomUUID().toString());
        auditEventRequest.setEventType(EventType.SUCCESS);
        auditEventRequest.setInitiator(INITIATOR);
        auditEventRequest.setCode(code);
        auditEventRequest.setCodeTitle(CODE_TITLE);
        auditEventRequest.setGroupCode(groupCode);
        auditEventRequest.setGroupTitle(GROUP_TITLE);
        auditEventRequest.setMessage(MESSAGE);
        auditEventRequest.setEventDate(LocalDateTime.now());
        return auditEventRequest;
    }
}
