package com.ecaservice.core.filter.query;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.web.dto.model.PageRequestDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

/**
 * Count query simple executor.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class CountQuerySimpleExecutor implements CountQueryExecutor {

    private final EntityManager entityManager;

    @Override
    public <T> long countQuery(AbstractFilter<T> filter, PageRequestDto pageRequestDto) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = query.from(filter.getEntityClass());
        Predicate predicate = filter.toPredicate(root, query, builder);
        query.select(builder.count(root));
        query.where(predicate);
        return entityManager.createQuery(query).getSingleResult();
    }
}
