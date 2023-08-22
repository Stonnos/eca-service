package com.ecaservice.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Experiment process persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "experiment_process")
public class ExperimentProcessEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Process uuid
     */
    @Column(name = "process_uuid", nullable = false, unique = true)
    private String processUuid;

    /**
     * Process status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "process_status", nullable = false)
    private ProcessStatus processStatus;

    /**
     * Process created date
     */
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Process start date
     */
    private LocalDateTime started;

    /**
     * Process finish date
     */
    private LocalDateTime finished;

    /**
     * Linked experiment
     */
    @ManyToOne
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;
}
