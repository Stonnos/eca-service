package com.ecaservice.model.experiment;

/**
 * Interface for visitor pattern.
 *
 * @param <T> - results generic type
 * @param <P> - params generic type
 * @author Roman Batygin
 */
public interface ExperimentStatusVisitor<T, P> {

    T caseNew(P parameter);

    T caseFinished(P parameter);

    T caseTimeout(P parameter);

    T caseError(P parameter);

    T caseFailed(P parameter);

    T caseExceeded(P parameter);
}
