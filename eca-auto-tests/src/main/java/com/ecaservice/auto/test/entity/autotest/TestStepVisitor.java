package com.ecaservice.auto.test.entity.autotest;

/**
 * Test step visitor interface.
 *
 * @author Roman Batygin
 */
public interface TestStepVisitor {

    /**
     * Handles email test step entity.
     *
     * @param emailTestStepEntity - email test step entity
     */
    default void visit(EmailTestStepEntity emailTestStepEntity) {
        throw new UnsupportedOperationException(
                String.format("Unsupported email step for request [%s]",
                        emailTestStepEntity.getEvaluationRequestEntity().getRequestId())
        );
    }

    /**
     * Handles experiment results test step entity.
     *
     * @param experimentResultsTestStepEntity - experiment results test step entity
     */
    default void visit(ExperimentResultsTestStepEntity experimentResultsTestStepEntity) {
        throw new UnsupportedOperationException(
                String.format("Unsupported experiment results step for request [%s]",
                        experimentResultsTestStepEntity.getEvaluationRequestEntity().getRequestId())
        );
    }

    /**
     * Handles evaluation results test step entity.
     *
     * @param evaluationResultsTestStepEntity - evaluation results test step entity
     */
    default void visit(EvaluationResultsTestStepEntity evaluationResultsTestStepEntity) {
        throw new UnsupportedOperationException(
                String.format("Unsupported evaluation results step for request [%s]",
                        evaluationResultsTestStepEntity.getEvaluationRequestEntity().getRequestId())
        );
    }
}
