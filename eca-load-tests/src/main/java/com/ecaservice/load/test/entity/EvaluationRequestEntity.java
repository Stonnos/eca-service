package com.ecaservice.load.test.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Evaluation request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "evaluation_request")
public class EvaluationRequestEntity extends BaseEntity {

    /**
     * Linked load test entity
     */
    @ManyToOne
    @JoinColumn(name = "load_test_id", nullable = false)
    private LoadTestEntity loadTestEntity;
}
