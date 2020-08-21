package com.ecaservice.load.test.entity;

import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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

    @Id
    @GeneratedValue
    private Long id;

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
     * Seed value for k * V cross - validation method
     */
    private Integer seed;

    /**
     * Test total time
     */
    @Column(name = "total_time")
    private Long totalTime;

    /**
     * Responses number received from eca - server
     */
    @Column(name = "num_responses")
    private Long numResponses;

    /**
     * Responses number with success status
     */
    @Column(name = "num_success_responses")
    private Long numSuccessRequests;

    /**
     * Responses number with error status
     */
    @Column(name = "num_error_responses")
    private Long numErrorRequests;

    /**
     * Test execution status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "execution_status")
    private ExecutionStatus executionStatus;
}
