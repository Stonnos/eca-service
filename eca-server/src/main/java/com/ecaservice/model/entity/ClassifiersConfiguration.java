package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Classifiers options configuration persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifiers_configuration")
public class ClassifiersConfiguration {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Configuration name
     */
    @Column(name = "_name")
    private String name;

    /**
     * Configuration created date
     */
    private LocalDateTime created;

    /**
     * Configuration updated date
     */
    private LocalDateTime updated;

    /**
     * Configuration source
     */
    @Enumerated(EnumType.STRING)
    private ClassifiersConfigurationSource source;

    /**
     * Is active?
     */
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean active;
}
