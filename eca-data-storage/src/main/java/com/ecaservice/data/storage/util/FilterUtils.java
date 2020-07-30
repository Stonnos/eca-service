package com.ecaservice.data.storage.util;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.entity.InstancesEntity_;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.MessageFormat;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Filter utility class.
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
     * Builds specification object.
     *
     * @param pageRequestDto - page request dto.
     * @return specification object
     */
    public static Specification<InstancesEntity> buildSpecification(PageRequestDto pageRequestDto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = newArrayList();
            if (StringUtils.isNotBlank(pageRequestDto.getSearchQuery())) {
                String trimQuery = pageRequestDto.getSearchQuery().trim().toLowerCase();
                predicates.add(cb.or(
                        buildLikePredicate(root, cb, InstancesEntity_.TABLE_NAME, trimQuery),
                        buildLikePredicate(root, cb, InstancesEntity_.CREATED_BY, trimQuery)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildLikePredicate(Root<InstancesEntity> root, CriteriaBuilder criteriaBuilder,
                                                String field,
                                                String value) {
        Expression<String> expression = root.get(field);
        return criteriaBuilder.like(criteriaBuilder.lower(expression), MessageFormat.format(LIKE_FORMAT, value));
    }
}
