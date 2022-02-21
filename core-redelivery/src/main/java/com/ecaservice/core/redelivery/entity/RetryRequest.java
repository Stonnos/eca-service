package com.ecaservice.core.redelivery.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Retry request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "retry_request")
public class RetryRequest {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Request type
     */
    @Column(name = "request_type", nullable = false)
    private String requestType;

    /**
     * Request id
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Request body
     */
    @Column(name = "request", columnDefinition = "text", nullable = false)
    private String request;

    /**
     * Transaction id (used for cross - system logging)
     */
    @Column(name = "tx_id")
    private String txId;

    /**
     * Current retries
     */
    private int retries;

    /**
     * Maximum retries
     */
    @Column(name = "max_retries")
    private int maxRetries;

    /**
     * Created date
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
