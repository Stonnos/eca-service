package com.ecaservice.external.api.test.entity;

import com.ecaservice.external.api.dto.RequestStatus;
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
     * Expected request status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "expected_request_status")
    private RequestStatus expectedRequestStatus;

    /**
     * Actual request status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "actual_request_status")
    private RequestStatus actualRequestStatus;

    /**
     * Request status match result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status_match_result")
    private MatchResult requestStatusMatchResult;

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
