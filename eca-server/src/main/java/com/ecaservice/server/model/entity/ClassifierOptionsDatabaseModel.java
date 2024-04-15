package com.ecaservice.server.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Classifier input options persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false, exclude = {"config", "configuration"})
@Entity
@Table(name = "classifier_options")
public class ClassifierOptionsDatabaseModel extends BaseEntity {

    /**
     * Options name
     */
    @Column(name = "options_name", nullable = false)
    private String optionsName;

    /**
     * Classifier options config as json
     */
    @Column(columnDefinition = "text", nullable = false)
    private String config;

    /**
     * Classifier options config MD5 hash
     */
    @Column(name = "config_md5_hash", nullable = false)
    private String configMd5Hash;

    /**
     * Classifiers configuration.
     */
    @ManyToOne
    @JoinColumn(name = "configuration_id", nullable = false)
    private ClassifiersConfiguration configuration;
}
