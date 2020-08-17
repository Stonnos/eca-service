package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

/**
 * Experiment progress persistence entity.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentProgressEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Is experiment processing finished?
     */
    private boolean finished;

    /**
     * Progress bar value in percentage
     */
    @Column(nullable = false)
    private Integer progress;

    /**
     * Linked experiment
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
