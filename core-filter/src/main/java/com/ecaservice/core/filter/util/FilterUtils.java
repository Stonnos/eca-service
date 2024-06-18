package com.ecaservice.core.filter.util;

import com.ecaservice.web.dto.model.SortFieldRequestDto;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.List;

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
     * @param sortFields       - sort fields
     * @param defaultField     - default field name to sort
     * @param defaultAscending - default sort direction
     * @return sort object
     */
    public static Sort buildSort(List<SortFieldRequestDto> sortFields, String defaultField, boolean defaultAscending) {
        if (CollectionUtils.isEmpty(sortFields)) {
            Sort sort = Sort.by(defaultField);
            return defaultAscending ? sort.ascending() : sort.descending();
        } else {
            Sort.Order[] orders = sortFields
                    .stream()
                    .map(sortFieldRequestDto -> sortFieldRequestDto.isAscending() ?
                            Sort.Order.asc(sortFieldRequestDto.getSortField()) :
                            Sort.Order.desc(sortFieldRequestDto.getSortField()))
                    .toArray(Sort.Order[]::new);
            return Sort.by(orders);
        }
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
            Join<T, ?> join = root.join(fieldLevels[0], JoinType.LEFT);
            return join.get(fieldLevels[1]);
        } else {
            return root.get(fieldName);
        }
    }
}
