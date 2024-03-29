package com.ecaservice.test.common.model;

/**
 * Field values match result enum.
 *
 * @author Roman Batygin
 */
public enum MatchResult {

    /**
     * All values match
     */
    MATCH,

    /**
     * Some values not match
     */
    NOT_MATCH,

    /**
     * Field value is null or empty
     */
    NOT_FOUND
}
