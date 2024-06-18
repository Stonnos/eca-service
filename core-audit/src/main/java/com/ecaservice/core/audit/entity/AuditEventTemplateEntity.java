package com.ecaservice.core.audit.entity;

import com.ecaservice.audit.dto.EventType;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import static com.ecaservice.core.audit.entity.Constraints.MESSAGE_TEMPLATE_LENGTH;

/**
 * Audit event template entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "audit_event_template",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"audit_code_id", "event_type"},
                        name = "audit_event_template_code_id_event_type_unique_index")
        })
public class AuditEventTemplateEntity {

    /**
     * Template id
     */
    @Id
    private Long id;

    /**
     * Audit event type.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    /**
     * Audit message template.
     */
    @Column(name = "message_template", nullable = false, length = MESSAGE_TEMPLATE_LENGTH)
    private String messageTemplate;

    /**
     * Audit code entity.
     */
    @ManyToOne
    @JoinColumn(name = "audit_code_id", nullable = false)
    private AuditCodeEntity auditCode;
}
