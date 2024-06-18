package com.ecaservice.auto.test.entity.ecaserver;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Base evaluation entity.
 *
 * @author Roman Batygin
 */
@Data
@MappedSuperclass
public class AbstractEvaluationEntity {

    @Id
    private Long id;

    /**
     * Request unique identifier
     */
    @Column(name = "request_id", nullable = false, unique = true, updatable = false)
    private String requestId;
}
