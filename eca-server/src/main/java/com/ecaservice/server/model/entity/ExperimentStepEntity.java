package com.ecaservice.server.model.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Experiment step persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "experiment_step", indexes = {
        @Index(columnList = "experiment_id, step", name = "experiment_step_experiment_id_step_unique_idx",
                unique = true),
        @Index(columnList = "experiment_id, step_order", name = "experiment_step_experiment_id_step_order_unique_idx",
                unique = true)
})
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
    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    /**
     * Created date
     */
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Started date
     */
    private LocalDateTime started;

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
     * Failed attempts number
     */
    @Column(name = "num_failed_attempts")
    private int numFailedAttempts;

    /**
     * Linked experiment
     */
    @ManyToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
