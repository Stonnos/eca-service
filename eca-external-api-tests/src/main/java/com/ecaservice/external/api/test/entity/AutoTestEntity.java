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
     * Request status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

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
