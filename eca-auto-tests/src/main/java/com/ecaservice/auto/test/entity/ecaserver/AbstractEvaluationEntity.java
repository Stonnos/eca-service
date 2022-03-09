package com.ecaservice.auto.test.entity.ecaserver;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

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
