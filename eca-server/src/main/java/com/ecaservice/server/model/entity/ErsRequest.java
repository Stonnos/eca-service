package com.ecaservice.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
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
     * Web - service request date
     */
    @Column(name = "request_date")
    private LocalDateTime requestDate;

    /**
     * Request id
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * Response status from web - service
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "response_status")
    private ErsResponseStatus responseStatus;

    /**
     * Error details
     */
    @Column(columnDefinition = "text")
    private String details;
}
