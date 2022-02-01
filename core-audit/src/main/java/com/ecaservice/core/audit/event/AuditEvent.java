package com.ecaservice.core.audit.event;

import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.model.AuditContextParams;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Audit event model.
 *
 * @author Roman Batygin
 */
@Getter
public class AuditEvent extends ApplicationEvent {

    /**
     * Audit code
     */
    private final String auditCode;

    /**
     * Event type
     */
    private final EventType eventType;

    /**
     * Correlation id
     */
    private final String correlationId;

    /**
     * Event initiator
     */
    private final String initiator;

    /**
     * Audit context params
     */
    private final AuditContextParams auditContextParams;

    /**
     * Create a new {@code AbstractNotificationEvent}.
     *
     * @param source             the object on which the event initially occurred or with
     *                           which the event is associated (never {@code null})
     * @param auditCode          - audit code
     * @param eventType          - event type
     * @param correlationId      - correlation id
     * @param initiator          - event initiator
     * @param auditContextParams - audit context params
     */
    public AuditEvent(Object source, String auditCode, EventType eventType, String correlationId, String initiator,
                      AuditContextParams auditContextParams) {
        super(source);
        this.auditCode = auditCode;
        this.eventType = eventType;
        this.correlationId = correlationId;
        this.initiator = initiator;
        this.auditContextParams = auditContextParams;
    }
}
