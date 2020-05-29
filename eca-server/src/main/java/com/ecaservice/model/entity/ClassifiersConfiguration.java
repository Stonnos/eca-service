package com.ecaservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "classifiers_configuration")
public class ClassifiersConfiguration extends BaseEntity {

    /**
     * Configuration name
     */
    @Column(name = "configuration_name")
    private String configurationName;

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
    @Column(name = "is_active")
    private boolean active;

    /**
     * Classifiers options list
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL)
    private List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels;
}
