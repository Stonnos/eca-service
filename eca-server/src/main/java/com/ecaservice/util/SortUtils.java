package com.ecaservice.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

/**
 * Sort utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class SortUtils {

    /**
     * Builds sort object.
     *
     * @param field     - field name to sort
     * @param ascending - sort direction
     * @return sort object
     */
    public static Sort buildSort(String field, boolean ascending) {
        if (StringUtils.isBlank(field)) {
            throw new IllegalArgumentException("Sort field isn't specified!");
        } else {
            Sort sort = Sort.by(field);
            return ascending ? sort.ascending() : sort.descending();
        }
    }
}
