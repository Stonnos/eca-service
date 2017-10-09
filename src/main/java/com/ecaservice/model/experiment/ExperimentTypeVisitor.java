package com.ecaservice.model.experiment;

/**
 * Interface for visitor pattern.
 *
 * @param <T> - generic type
 * @author Roman Batygin
 */
public interface ExperimentTypeVisitor<T, P> {

    T caseNeuralNetwork(P parameter);

    T caseHeterogeneousEnsemble(P parameter);

    T caseModifiedHeterogeneousEnsemble(P parameter);

    T caseAdaBoost(P parameter);

    T caseStacking(P parameter);

    T caseKNearestNeighbours(P parameter);

    void afterHandle(T result, P parameter);

}
