package com.ecaservice.core.audit.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
