package com.ecaservice.code.audit.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

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
    @JoinColumn(name = "audit_group_id")
    private AuditGroupEntity group;

    /**
     * Audit events
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "audit_code_id", nullable = false)
    private List<AuditEventEntity> events;
}
