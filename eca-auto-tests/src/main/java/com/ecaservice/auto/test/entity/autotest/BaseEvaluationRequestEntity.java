package com.ecaservice.auto.test.entity.autotest;

import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

import static com.ecaservice.auto.test.util.Constraints.DOWNLOAD_URL_MAX_LENGTH;

/**
 * Base evaluation request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "base_evaluation_request")
@Inheritance(strategy = InheritanceType.JOINED)
public class BaseEvaluationRequestEntity extends BaseTestEntity {

    /**
     * Request id from eca - server
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Evaluation method
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_method")
    private EvaluationMethod evaluationMethod;

    /**
     * Message correlation id
     */
    @Column(name = "correlation_id", unique = true, updatable = false)
    private String correlationId;

    /**
     * Request stage
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "stage_type")
    private RequestStageType stageType;

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

    /**
     * Test steps
     */
    @OneToMany(mappedBy = "evaluationRequestEntity")
    private List<BaseTestStepEntity> testSteps;

    /**
     * Model download url
     */
    @Column(name = "download_url", length = DOWNLOAD_URL_MAX_LENGTH)
    private String downloadUrl;
}
