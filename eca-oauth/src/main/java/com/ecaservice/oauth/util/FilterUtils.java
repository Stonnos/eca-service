package com.ecaservice.oauth.util;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserEntity_;
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
    public static Specification<UserEntity> buildSpecification(PageRequestDto pageRequestDto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = newArrayList();
            if (StringUtils.isNotBlank(pageRequestDto.getSearchQuery())) {
                String trimQuery = pageRequestDto.getSearchQuery().trim().toLowerCase();
                predicates.add(cb.or(
                        buildLikePredicate(root, cb, UserEntity_.LOGIN, trimQuery),
                        buildLikePredicate(root, cb, UserEntity_.EMAIL, trimQuery),
                        buildLikePredicate(root, cb, UserEntity_.FULL_NAME, trimQuery))
                );
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
