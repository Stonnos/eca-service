package com.ecaservice.data.storage.util;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.entity.InstancesEntity_;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.List;

import static com.ecaservice.core.filter.util.FilterUtils.buildLikePredicate;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Filter utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FilterUtils {

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
}
