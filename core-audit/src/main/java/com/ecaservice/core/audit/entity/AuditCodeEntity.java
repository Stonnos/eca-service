package com.ecaservice.core.audit.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Audit code entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "audit_code")
public class AuditCodeEntity extends BaseAuditEntity {

    /**
     * Audit code enabled?
     */
    @Column(nullable = false)
    private boolean enabled;

    /**
     * Audit group id
     */
    @ManyToOne
    @JoinColumn(name = "audit_group_id", nullable = false)
    private AuditGroupEntity auditGroup;
}
