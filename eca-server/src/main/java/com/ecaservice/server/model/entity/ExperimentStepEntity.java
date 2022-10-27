package com.ecaservice.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Experiment step persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "experiment_step")
public class ExperimentStepEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Step
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperimentStep step;

    /**
     * Step status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperimentStepStatus status;

    /**
     * Step order
     */
    private int order;

    /**
     * Completed date
     */
    private LocalDateTime completed;

    /**
     * Error message
     */
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    /**
     * Linked experiment
     */
    @OneToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
