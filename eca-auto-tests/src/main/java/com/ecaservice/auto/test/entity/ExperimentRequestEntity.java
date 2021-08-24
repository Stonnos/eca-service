package com.ecaservice.auto.test.entity;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.test.common.model.MatchResult;
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
    private ExperimentRequestStageType stageType;

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
     * Experiment NEW status email received?
     */
    @Column(name = "new_status_email_received")
    private boolean newStatusEmailReceived;

    /**
     * Experiment IN_PROGRESS status email received?
     */
    @Column(name = "in_progress_status_email_received")
    private boolean inProgressStatusEmailReceived;

    /**
     * Experiment FINISHED status email received?
     */
    @Column(name = "finished_status_email_received")
    private boolean finishedStatusEmailReceived;

    /**
     * Experiment ERROR status email received?
     */
    @Column(name = "error_status_email_received")
    private boolean errorStatusEmailReceived;

    /**
     * Experiment TIMEOUT status email received?
     */
    @Column(name = "timeout_status_email_received")
    private boolean timeoutStatusEmailReceived;

    /**
     * Experiment download url
     */
    @Column(name = "download_url")
    private String downloadUrl;

    /**
     * Linked job entity
     */
    @ManyToOne
    @JoinColumn(name = "auto_tests_job_id", nullable = false)
    private AutoTestsJobEntity job;

    /**
     * Total matched
     */
    @Column(name = "total_matched")
    private int totalMatched;

    /**
     * Total not matched
     */
    @Column(name = "total_not_matched")
    private int totalNotMatched;

    /**
     * Total not found
     */
    @Column(name = "total_not_found")
    private int totalNotFound;

    /**
     * Expected experiment results size
     */
    @Column(name = "expected_results_size")
    private int expectedResultsSize;

    /**
     * Actual experiment results size
     */
    @Column(name = "actual_results_size")
    private int actualResultsSize;

    /**
     * Experiment results size match result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "results_size_match_result")
    private MatchResult resultsSizeMatchResult;
}
