package com.ecaservice.server.model.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * ERS web - service request base entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "ers_request")
@Inheritance(strategy = InheritanceType.JOINED)
public class ErsRequest {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Request date
     */
    @Column(name = "request_date")
    private LocalDateTime requestDate;

    /**
     * Request id
     */
    @Column(name = "request_id", unique = true, nullable = false)
    private String requestId;

    /**
     * Response status from web - service
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "response_status", nullable = false)
    private ErsResponseStatus responseStatus;

    /**
     * Error details
     */
    @Column(columnDefinition = "text")
    private String details;
}
