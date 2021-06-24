package com.ecaservice.core.filter.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.MessageFormat;

/**
 * Sort utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FilterUtils {

    private static final String LIKE_FORMAT = "%{0}%";

    /**
     * Builds sort object.
     *
     * @param field        - field name to sort
     * @param defaultField - default field name to sort
     * @param ascending    - sort direction
     * @return sort object
     */
    public static Sort buildSort(String field, String defaultField, boolean ascending) {
        String sortField = !StringUtils.isBlank(field) ? field : defaultField;
        Sort sort = Sort.by(sortField);
        return ascending ? sort.ascending() : sort.descending();
    }

    /**
     * Builds like predicate.
     *
     * @param root            - root object
     * @param criteriaBuilder - criteria builder object
     * @param field           - field name
     * @param value           - field value
     * @param <T>             - entity type
     * @return like predicate
     */
    public static <T> Predicate buildLikePredicate(Root<T> root, CriteriaBuilder criteriaBuilder, String field,
                                                   String value) {
        Expression<String> expression = root.get(field);
        return criteriaBuilder.like(criteriaBuilder.lower(expression), MessageFormat.format(LIKE_FORMAT, value));
    }
}
