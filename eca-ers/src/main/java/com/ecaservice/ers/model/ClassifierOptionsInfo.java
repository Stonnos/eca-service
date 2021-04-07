package com.ecaservice.ers.model;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Map;

/**
 * Classifier options info persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifier_options_info")
public class ClassifierOptionsInfo {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Classifier name.
     */
    @Column(name = "classifier_name", nullable = false)
    private String classifierName;

    /**
     * Classifier options (input options config)
     */
    @Column(columnDefinition = "text", nullable = false)
    private String options;

    /**
     * Classifier description (additional information).
     */
    @Column(name = "classifier_description")
    private String classifierDescription;

    /**
     * Is meta classifier (used for stacking algorithms)?
     */
    @Column(name = "meta_classifier")
    private boolean metaClassifier;

    /**
     * Classifier input options map
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "input_options")
    @MapKeyColumn(name = "option_name")
    @Column(name = "option_value")
    private Map<String, String> inputOptionsMap;

    /**
     * Individual classifiers options for ensemble algorithms
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private List<ClassifierOptionsInfo> individualClassifiers;
}
