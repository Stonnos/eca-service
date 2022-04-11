package com.ecaservice.auto.test.entity.ecaserver;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * ERS request base entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "ers_request")
@Inheritance(strategy = InheritanceType.JOINED)
public class ErsRequest {

    @Id
    private Long id;

    /**
     * Request id
     */
    @Column(name = "request_id", unique = true, nullable = false, updatable = false)
    private String requestId;

    /**
     * Ers response status
     */
    @Column(name = "response_status", nullable = false, updatable = false)
    private String responseStatus;
}
