package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

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
     * Is build in?
     */
    @Column(name = "build_in")
    private boolean buildIn;

    /**
     * Is active?
     */
    @Column(name = "_active")
    private boolean active;

    /**
     * Classifiers options list
     */
    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL)
    private List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels;
}
