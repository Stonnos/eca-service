package com.ecaservice.auto.test.entity;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.test.common.model.RequestStageType;
import com.ecaservice.test.common.model.TestResult;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Experiment request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "experiment_request")
public class ExperimentRequestEntity extends BaseEntity {

    /**
     * Experiment request id
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Experiment type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "experiment_type")
    private ExperimentType experimentType;

    /**
     * Evaluation method
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_method")
    private EvaluationMethod evaluationMethod;

    /**
     * Message correlation id
     */
    @Column(name = "correlation_id")
    private String correlationId;

    /**
     * Request stage
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "stage_type")
    private RequestStageType stageType;

    /**
     * Test result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "test_result")
    private TestResult testResult;

    /**
     * Instances name
     */
    @Column(name = "relation_name")
    private String relationName;

    /**
     * Instances number
     */
    @Column(name = "num_instances")
    private Integer numInstances;

    /**
     * Attributes number
     */
    @Column(name = "num_attributes")
    private Integer numAttributes;

    /**
     * Linked job entity
     */
    @ManyToOne
    @JoinColumn(name = "auto_tests_job_id", nullable = false)
    private AutoTestsJobEntity job;
}