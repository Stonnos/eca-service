package com.ecaservice.auto.test.entity.autotest;

import com.ecaservice.auto.test.model.evaluation.EvaluationResultsDetailsMatch;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

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
    @Type(type = "jsonb")
    @Column(name = "evaluation_results_details", columnDefinition = "jsonb")
    private EvaluationResultsDetailsMatch evaluationResultsDetails;
}
