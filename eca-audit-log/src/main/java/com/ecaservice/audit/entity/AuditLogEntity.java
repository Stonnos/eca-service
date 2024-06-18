package com.ecaservice.audit.entity;

import com.ecaservice.audit.dto.EventType;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Audit log persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "audit_log")
public class AuditLogEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Audit event id.
     */
    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    /**
     * Correlation id.
     */
    @Column(name = "correlation_id")
    private String correlationId;

    /**
     * Audit message
     */
    @Column(nullable = false, columnDefinition = "text")
    private String message;

    /**
     * Event initiator
     */
    @Column(nullable = false)
    private String initiator;

    /**
     * Event type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    /**
     * Audit group
     */
    @Column(name = "group_code", nullable = false)
    private String groupCode;

    /**
     * Audit group title
     */
    @Column(name = "group_title")
    private String groupTitle;

    /**
     * Audit code
     */
    @Column(name = "audit_code", nullable = false)
    private String code;

    /**
     * Audit code title
     */
    @Column(name = "audit_code_title")
    private String codeTitle;

    /**
     * Event date
     */
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
}
