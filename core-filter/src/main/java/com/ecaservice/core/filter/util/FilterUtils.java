package com.ecaservice.core.filter.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import static com.ecaservice.core.filter.util.Utils.splitByPointSeparator;

/**
 * Sort utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FilterUtils {

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
     * Builds expression for specified field name
     *
     * @param root      - {@link Root} object
     * @param fieldName - field name
     * @param <T>       - entity class
     * @param <E>       - filter field class
     * @return expression object
     */
    public static <T, E> Expression<E> buildExpression(Root<T> root, String fieldName) {
        String[] fieldLevels = splitByPointSeparator(fieldName);
        if (fieldLevels != null && fieldLevels.length > 1) {
            Join<T, ?> join = root.join(fieldLevels[0]);
            return join.get(fieldLevels[1]);
        } else {
            return root.get(fieldName);
        }
    }
}
