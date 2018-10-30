package com.ecaservice.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

/**
 * Sort utility class.
 *
 * @author Roman Batygin
 */
public class SortUtils {

    private SortUtils() {
    }

    /**
     * Builds sort object.
     *
     * @param field        - field name to sort
     * @param ascending    - sort direction
     * @param defaultField - default field in case if sort field name isn't specified
     * @return sort object
     */
    public static Sort buildSort(String field, boolean ascending, String defaultField) {
        if (StringUtils.isEmpty(field)) {
            return Sort.by(Sort.Order.desc(defaultField));
        } else {
            if (ascending) {
                return Sort.by(Sort.Order.asc(field));
            } else {
                return Sort.by(Sort.Order.desc(field));
            }
        }
    }
}
