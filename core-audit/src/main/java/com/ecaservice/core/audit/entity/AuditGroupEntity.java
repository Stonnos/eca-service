package com.ecaservice.core.audit.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Audit group entity. Used for grouping audit codes.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "audit_group")
public class AuditGroupEntity extends BaseAuditEntity {
}
