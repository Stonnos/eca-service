package com.ecaservice.core.filter.query;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.web.dto.model.PageRequestDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

/**
 * Filter query executor.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class FilterQueryExecutor {

    private final EntityManager entityManager;

    /**
     * Executes page query.
     *
     * @param pageRequestDto     - page request dto
     * @param filter             - filter object
     * @param pageRequest        - page request domain
     * @param countQueryExecutor - count query executor
     * @param <T>                - entity generic type
     * @return entities page
     */
    public <T> Page<T> executePageQuery(PageRequestDto pageRequestDto,
                                        AbstractFilter<T> filter,
                                        PageRequest pageRequest,
                                        CountQueryExecutor countQueryExecutor) {
        List<T> result = executeListQuery(filter, pageRequest, filter.getEntityClass());
        return PageableExecutionUtils.getPage(result, pageRequest,
                () -> countQueryExecutor.countQuery(filter, pageRequestDto));
    }

    /**
     * Executes list query.
     *
     * @param filter      - filter object
     * @param pageRequest - page request
     * @param entityClass - entity class
     * @param <T>         - entity generic type
     * @return entities list
     */
    public <T> List<T> executeListQuery(Specification<T> filter,
                                        PageRequest pageRequest,
                                        Class<T> entityClass) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        Predicate predicate = filter.toPredicate(root, query, builder);
        query.select(root);
        query.where(predicate);
        query.orderBy(toOrders(pageRequest.getSort(), root, builder));
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(PageableUtils.getOffsetAsInteger(pageRequest));
        typedQuery.setMaxResults(pageRequest.getPageSize());
        return typedQuery.getResultList();
    }
}
