package com.ecaservice.server.model.evaluation;

/**
 * Evaluation request data visitor interface.
 *
 * @author Roman Batygin
 */
public interface EvaluationRequestDataVisitor {

    /**
     * Visit evaluation web request data model.
     *
     * @param evaluationWebRequestDataModel - evaluation web request data model
     */
    default void visit(EvaluationWebRequestDataModel evaluationWebRequestDataModel) {
    }

    /**
     * Visit evaluation message request data model.
     *
     * @param evaluationMessageRequestDataModel - evaluation message request data model
     */
    default void visit(EvaluationMessageRequestDataModel evaluationMessageRequestDataModel) {
    }
}
