package com.ecaservice.data.storage.entity;

/**
 * Attribute type visitor.
 *
 * @param <T> - generic  type
 * @author Roman Batygin
 */
public interface AttributeTypeVisitor<T> {

    /**
     * Case if attribute type is NUMERIC.
     *
     * @return generic object
     */
    T caseNumeric();

    /**
     * Case if attribute type is NOMINAL.
     *
     * @return generic object
     */
    T caseNominal();

    /**
     * Case if attribute type is DATE.
     *
     * @return generic object
     */
    T caseDate();
}
