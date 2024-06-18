package com.ecaservice.mail.model;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Base entity model.
 *
 * @author Roman Batygin
 */
@Data
@MappedSuperclass
abstract class BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Creation date
     */
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
