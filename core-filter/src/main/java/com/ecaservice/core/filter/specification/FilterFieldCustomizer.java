package com.ecaservice.core.filter.specification;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Filter field customizer.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FilterFieldCustomizer {

    private final String fieldName;

    /**
     * Can handle field?
     *
     * @param fieldName - field name
     * @return {@code true} in case if field name can be handle, otherwise {@code false}
     */
    public boolean canHandle(String fieldName) {
        return this.fieldName.equals(fieldName);
    }

    /**
     * Builds filter predicate.
     *
     * @param root            - {@link Root} object
     * @param criteriaBuilder - criteria builder object
     * @param value           - filter field value
     * @return predicate object
     */
    public abstract Predicate toPredicate(Root<?> root, CriteriaBuilder criteriaBuilder, String value);
}
