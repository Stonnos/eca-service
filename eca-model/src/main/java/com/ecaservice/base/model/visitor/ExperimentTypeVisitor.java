package com.ecaservice.base.model.visitor;

/**
 * Interface for visitor pattern.
 *
 * @param <T> - results generic type
 * @param <P> - params generic type
 * @author Roman Batygin
 */
public interface ExperimentTypeVisitor<T, P> {

    /**
     * Method executed in case if experiment type is NEURAL_NETWORK.
     *
     * @param parameter input parameter
     * @return generic object
     */
    T caseNeuralNetwork(P parameter);

    /**
     * Method executed in case if experiment type is HETEROGENEOUS_ENSEMBLE.
     *
     * @param parameter input parameter
     * @return generic object
     */
    T caseHeterogeneousEnsemble(P parameter);

    /**
     * Method executed in case if experiment type is MODIFIED_HETEROGENEOUS_ENSEMBLE.
     *
     * @param parameter input parameter
     * @return generic object
     */
    T caseModifiedHeterogeneousEnsemble(P parameter);

    /**
     * Method executed in case if experiment type is ADA_BOOST.
     *
     * @param parameter input parameter
     * @return generic object
     */
    T caseAdaBoost(P parameter);

    /**
     * Method executed in case if experiment type is STACKING.
     *
     * @param parameter input parameter
     * @return generic object
     */
    T caseStacking(P parameter);

    /**
     * Method executed in case if experiment type is KNN.
     *
     * @param parameter input parameter
     * @return generic object
     */
    T caseKNearestNeighbours(P parameter);

    /**
     * Method executed in case if experiment type is Random forests.
     *
     * @param parameter input parameter
     * @return generic object
     */
    T caseRandomForests(P parameter);

    /**
     * Method executed in case if experiment type is STACKING_CV.
     *
     * @param parameter input parameter
     * @return generic object
     */
    T caseStackingCV(P parameter);

    /**
     * Method executed in case if experiment type is DECISION_TREE.
     *
     * @param parameter input parameter
     * @return generic object
     */
    T caseDecisionTree(P parameter);

    /**
     * Method that calls after common handling.
     *
     * @param result    results
     * @param parameter input parameter
     */
    void afterHandle(T result, P parameter);
}
