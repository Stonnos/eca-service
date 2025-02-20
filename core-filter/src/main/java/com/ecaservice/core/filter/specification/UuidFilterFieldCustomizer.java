package com.ecaservice.core.filter.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import static com.ecaservice.web.dto.util.FieldConstraints.UUID_PATTERN;

/**
 * Uuid filter field customizer.
 *
 * @author Roman Batygin
 */
public class UuidFilterFieldCustomizer extends FilterFieldCustomizer {

    /**
     * Constructor with spring dependency injection.
     *
     * @param fieldName - field name
     */
    public UuidFilterFieldCustomizer(String fieldName) {
        super(fieldName);
    }

    @Override
    public Predicate toPredicate(Root<?> root, CriteriaBuilder criteriaBuilder, String value) {
        if (!value.matches(UUID_PATTERN)) {
            return null;
        }
        return criteriaBuilder.equal(root.get(getFieldName()), value);
    }
}
