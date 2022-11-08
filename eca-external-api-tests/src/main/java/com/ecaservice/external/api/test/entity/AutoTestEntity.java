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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Base auto test persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "auto_test")
@Inheritance(strategy = InheritanceType.JOINED)
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
