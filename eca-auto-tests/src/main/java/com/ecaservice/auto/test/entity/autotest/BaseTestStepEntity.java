package com.ecaservice.auto.test.entity.autotest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Base test step persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "test_step")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "step_type")
public abstract class BaseTestStepEntity<E extends BaseEvaluationRequestEntity> extends BaseTestEntity {

    /**
     * Evaluation request entity
     */
    @ToString.Exclude
    @ManyToOne(targetEntity = BaseEvaluationRequestEntity.class)
    @JoinColumn(name = "evaluation_request_id", nullable = false)
    private E evaluationRequestEntity;

    /**
     * Calls test step visitor.
     *
     * @param visitor - test step visitor
     */
    public abstract void visit(TestStepVisitor visitor);
}
