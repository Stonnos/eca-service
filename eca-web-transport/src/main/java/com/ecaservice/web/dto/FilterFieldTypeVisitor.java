package com.ecaservice.web.dto;

/**
 * Visitor interface for filter field type.
 *
 * @param <T> - generic type
 * @author Roman Batygin
 */
public interface FilterFieldTypeVisitor<T> {

    /**
     * Method executed in case if match mode is TEXT.
     *
     * @return generic object
     */
    T caseText();

    /**
     * Method executed in case if match mode is DATE.
     *
     * @return generic object
     */
    T caseDate();

    /**
     * Method executed in case if match mode is REFERENCE.
     *
     * @return generic object
     */
    T caseReference();
}
