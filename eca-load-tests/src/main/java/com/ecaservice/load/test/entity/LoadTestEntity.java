package com.ecaservice.load.test.entity;

import com.ecaservice.test.common.model.ExecutionStatus;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Load test persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "load_test")
public class LoadTestEntity extends BaseEntity {

    /**
     * Load test uuid
     */
    @Column(name = "test_uuid", nullable = false)
    private String testUuid;

    /**
     * Test creation date
     */
    private LocalDateTime created;

    /**
     * Requests number to eca - server
     */
    @Column(name = "num_requests")
    private Integer numRequests;

    /**
     * Threads number for requests sending
     */
    @Column(name = "num_threads")
    private Integer numThreads;

    /**
     * Evaluation method
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_method")
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Column(name = "num_folds")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Column(name = "num_tests")
    private Integer numTests;

    /**
     * Seed value for random generator
     */
    private Integer seed;

    /**
     * Test execution status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "execution_status")
    private ExecutionStatus executionStatus;

    /**
     * Details string
     */
    @Column(columnDefinition = "text")
    private String details;
}
