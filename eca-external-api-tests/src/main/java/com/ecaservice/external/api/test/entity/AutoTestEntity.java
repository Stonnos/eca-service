package com.ecaservice.external.api.test.entity;

import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.test.common.model.MatchResult;
import com.ecaservice.test.common.model.TestResult;
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
import java.math.BigDecimal;

import static com.ecaservice.external.api.test.util.Constraints.PRECISION;
import static com.ecaservice.external.api.test.util.Constraints.SCALE;

/**
 * Auto test persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "auto_test")
public class AutoTestEntity extends BaseEntity {

    /**
     * Request id
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Display name (Test description)
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * Request json
     */
    @Column(columnDefinition = "text")
    private String request;

    /**
     * Response json
     */
    @Column(columnDefinition = "text")
    private String response;

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
     * Expected response code
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "expected_response_code")
    private ResponseCode expectedResponseCode;

    /**
     * Actual response code
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "actual_response_code")
    private ResponseCode actualResponseCode;

    /**
     * Response code match result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "response_code_match_result")
    private MatchResult responseCodeMatchResult;

    /**
     * Expected model url
     */
    @Column(name = "expected_model_url")
    private String expectedModelUrl;

    /**
     * Actual model url
     */
    @Column(name = "actual_model_url")
    private String actualModelUrl;

    /**
     * Model url match result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "model_url_match_result")
    private MatchResult modelUrlMatchResult;

    /**
     * Expected pct correct
     */
    @Column(name = "expected_pct_correct", precision = PRECISION, scale = SCALE)
    private BigDecimal expectedPctCorrect;

    /**
     * Actual pct correct
     */
    @Column(name = "actual_pct_correct", precision = PRECISION, scale = SCALE)
    private BigDecimal actualPctCorrect;

    /**
     * Pct correct match result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pct_correct_match_result")
    private MatchResult pctCorrectMatchResult;

    /**
     * Expected pct incorrect
     */
    @Column(name = "expected_pct_incorrect", precision = PRECISION, scale = SCALE)
    private BigDecimal expectedPctIncorrect;

    /**
     * Actual pct incorrect
     */
    @Column(name = "actual_pct_incorrect", precision = PRECISION, scale = SCALE)
    private BigDecimal actualPctIncorrect;

    /**
     * Pct incorrect match result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pct_incorrect_match_result")
    private MatchResult pctIncorrectMatchResult;

    /**
     * Expected mean absolute error
     */
    @Column(name = "expected_mean_absolute_error", precision = PRECISION, scale = SCALE)
    private BigDecimal expectedMeanAbsoluteError;

    /**
     * Actual mean absolute error
     */
    @Column(name = "actual_mean_absolute_error", precision = PRECISION, scale = SCALE)
    private BigDecimal actualMeanAbsoluteError;

    /**
     * Mean absolute error match result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "mean_absolute_error_match_result")
    private MatchResult meanAbsoluteErrorMatchResult;

    /**
     * Test result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "test_result")
    private TestResult testResult;

    /**
     * Linked job entity
     */
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private JobEntity job;
}
