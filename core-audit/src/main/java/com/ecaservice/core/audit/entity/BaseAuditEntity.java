package com.ecaservice.core.audit.entity;

import lombok.Data;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Base audit entity.
 *
 * @author Roman Batygin
 */
@Data
@MappedSuperclass
public abstract class BaseAuditEntity {

    /**
     * Audit unit unique identifier
     */
    @Id
    private String id;

    /**
     * Audit unit title string
     */
    private String title;
}
