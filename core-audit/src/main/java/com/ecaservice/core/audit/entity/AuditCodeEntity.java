package com.ecaservice.core.audit.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Audit code entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "audit_code",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id", "audit_group_id"},
                        name = "audit_code_id_audit_group_id_unique_index")
        })
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
    private AuditGroupEntity group;
}
