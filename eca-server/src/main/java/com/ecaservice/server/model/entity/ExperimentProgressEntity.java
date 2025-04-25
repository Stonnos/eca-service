package com.ecaservice.server.model.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Experiment progress persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "experiment_progress")
public class ExperimentProgressEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Is experiment processing finished?
     */
    private boolean finished;

    /**
     * Is experiment processing canceled?
     */
    private boolean canceled;

    /**
     * Progress bar value in percentage
     */
    @Column(nullable = false)
    private Integer progress;

    /**
     * Linked experiment
     */
    @OneToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
