package com.ecaservice.auto.test.entity.autotest;

import com.ecaservice.auto.test.model.evaluation.EvaluationResultsDetailsMatch;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Evaluation results test step persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("EVALUATION_RESULTS_STEP")
public class EvaluationResultsTestStepEntity extends BaseTestStepEntity<EvaluationRequestEntity> {

    /**
     * Evaluation results details
     */
    @Type(JsonType.class)
    @Column(name = "evaluation_results_details", columnDefinition = "jsonb")
    private EvaluationResultsDetailsMatch evaluationResultsDetails;

    @Override
    public void visit(TestStepVisitor visitor) {
        visitor.visit(this);
    }
}
