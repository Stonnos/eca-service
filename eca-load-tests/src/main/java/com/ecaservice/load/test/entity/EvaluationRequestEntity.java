package com.ecaservice.load.test.entity;

import com.ecaservice.test.common.model.TestResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Evaluation request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "evaluation_request")
public class EvaluationRequestEntity extends BaseEntity {

    /**
     * Message correlation id
     */
    @Column(name = "correlation_id", unique = true)
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
     * Classifier name
     */
    @Column(name = "classifier_name")
    private String classifierName;

    /**
     * Classifier options json config
     */
    @Column(name = "classifier_options", columnDefinition = "text")
    private String classifierOptions;

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
     * Linked load test entity
     */
    @ManyToOne
    @JoinColumn(name = "load_test_id", nullable = false)
    private LoadTestEntity loadTestEntity;
}
