package com.ecaservice.load.test.entity;

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
     * Test finished date
     */
    private LocalDateTime finished;

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
     * Test total time
     */
    @Column(name = "total_time")
    private Long totalTime;

    /**
     * Test execution status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "execution_status")
    private ExecutionStatus executionStatus;
}
