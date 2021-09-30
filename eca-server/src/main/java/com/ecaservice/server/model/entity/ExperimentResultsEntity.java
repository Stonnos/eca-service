package com.ecaservice.server.model.entity;

import com.ecaservice.server.util.FieldConstraints;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Experiment results persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "experiment_results")
@NamedEntityGraph(name = "classifierInfo",
        attributeNodes = {@NamedAttributeNode(value = "classifierInfo", subgraph = "classifierInputOptions")},
        subgraphs = {@NamedSubgraph(name = "classifierInputOptions",
                attributeNodes = {@NamedAttributeNode("classifierInputOptions")})})
public class ExperimentResultsEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Experiment results index
     */
    @Column(name = "results_index", nullable = false)
    private Integer resultsIndex;

    /**
     * Classifier info
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "classifier_info_id", nullable = false)
    private ClassifierInfo classifierInfo;

    /**
     * Correctly classified percentage
     */
    @Column(name = "pct_correct", precision = FieldConstraints.PRECISION, scale = FieldConstraints.SCALE)
    private BigDecimal pctCorrect;

    /**
     * Experiment entity
     */
    @ManyToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
