package com.ecaservice.model.experiment;

/**
 * Interface for visitor pattern.
 *
 * @param <T> - results generic type
 * @param <P> - params generic type
 * @author Roman Batygin
 */
public interface ExperimentStatusVisitor<T, P> {

    /**
     * Method executed in case if experiment status is NEW.
     * @param parameter input parameter
     * @return generic object
     */
    T caseNew(P parameter);

    /**
     * Method executed in case if experiment status is FINISHED.
     * @param parameter input parameter
     * @return generic object
     */
    T caseFinished(P parameter);

    /**
     * Method executed in case if experiment status is TIMEOUT.
     * @param parameter input parameter
     * @return generic object
     */
    T caseTimeout(P parameter);

    /**
     * Method executed in case if experiment status is ERROR.
     * @param parameter input parameter
     * @return generic object
     */
    T caseError(P parameter);

    /**
     * Method executed in case if experiment status is EXCEEDED.
     * @param parameter input parameter
     * @return generic object
     */
    T caseExceeded(P parameter);

}
