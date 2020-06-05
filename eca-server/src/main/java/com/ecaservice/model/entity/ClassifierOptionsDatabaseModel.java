package com.ecaservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
    @Column(name = "options_name")
    private String optionsName;

    /**
     * Classifier options config as json
     */
    @Column(columnDefinition = "text")
    private String config;

    /**
     * Classifier options config MD5 hash
     */
    @Column(name = "config_md5_hash")
    private String configMd5Hash;

    /**
     * Classifiers configuration.
     */
    @ManyToOne
    @JoinColumn(name = "configuration_id", nullable = false)
    private ClassifiersConfiguration configuration;
}
