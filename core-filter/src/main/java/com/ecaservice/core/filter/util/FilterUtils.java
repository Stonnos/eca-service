package com.ecaservice.core.filter.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

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
}
