package com.ecaservice.web.dto;

/**
 * Visitor interface for match mode.
 *
 * @param <T> - generic type
 * @author Roman Batygin
 */
public interface MatchModeVisitor<T> {

    /**
     * Method executed in case if match mode is EQUALS.
     *
     * @return generic object
     */
    T caseEquals();

    /**
     * Method executed in case if match mode is GTE.
     *
     * @return generic object
     */
    T caseGte();

    /**
     * Method executed in case if match mode is LTE.
     *
     * @return generic object
     */
    T caseLte();

    /**
     * Method executed in case if match mode is LIKE.
     *
     * @return generic object
     */
    T caseLike();
}
