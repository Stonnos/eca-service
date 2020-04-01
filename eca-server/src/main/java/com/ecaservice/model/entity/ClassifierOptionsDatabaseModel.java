package com.ecaservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Classifier input options persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(exclude = {"id", "creationDate", "config"})
@Entity
@Table(name = "classifier_options")
public class ClassifierOptionsDatabaseModel {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Options name
     */
    @Column(name = "options_name")
    private String optionsName;

    /**
     * Config creation date
     */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

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
