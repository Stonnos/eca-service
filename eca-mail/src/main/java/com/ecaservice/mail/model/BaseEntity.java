package com.ecaservice.mail.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
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
