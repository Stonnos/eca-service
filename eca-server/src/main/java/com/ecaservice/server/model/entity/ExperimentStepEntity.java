package com.ecaservice.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
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
@Table(name = "experiment_step", indexes = {
        @Index(columnList = "experiment_id, step", name = "experiment_step_experiment_id_step_unique_idx"),
        @Index(columnList = "experiment_id, step_order", name = "experiment_step_experiment_id_step_order_unique_idx")
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
    @OneToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
