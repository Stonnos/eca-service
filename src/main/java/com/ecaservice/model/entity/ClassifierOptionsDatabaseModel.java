package com.ecaservice.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Classifier input options persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(exclude = {"id", "version", "creationDate"})
@Entity
@Table(name = "classifier_options")
public class ClassifierOptionsDatabaseModel {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Config version
     */
    private int version;

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
    @Lob
    private String config;
}
