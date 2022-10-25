package com.ecaservice.server.util;

import lombok.experimental.UtilityClass;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.ecaservice.server.util.Utils.atEndOfDay;
import static com.ecaservice.server.util.Utils.atStartOfDay;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Query helper class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class QueryHelper {

    /**
     * Builds group by statistics query.
     *
     * @param builder           - criteria builder
     * @param entityClazz       - entity class
     * @param groupByExpression - group by expression function
     * @param dateAttribute     - date attribute for where clause
     * @param dateFrom          - date from
     * @param dateTo            - date to
     * @param <T>               - entity class
     * @return criteria query
     */
    public static <T> CriteriaQuery<Tuple> buildGroupByStatisticsQuery(CriteriaBuilder builder,
                                                                       Class<T> entityClazz,
                                                                       Function<Root<T>, Expression<?>> groupByExpression,
                                                                       String dateAttribute,
                                                                       LocalDate dateFrom,
                                                                       LocalDate dateTo) {
        CriteriaQuery<Tuple> criteria = builder.createQuery(Tuple.class);
        Root<T> root = criteria.from(entityClazz);
        List<Predicate> predicates = newArrayList();
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add(
                builder.greaterThanOrEqualTo(root.get(dateAttribute), atStartOfDay(value))));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add(
                builder.lessThanOrEqualTo(root.get(dateAttribute), atEndOfDay(value))));
        var groupBy = groupByExpression.apply(root);
        criteria.groupBy(groupBy);
        criteria.multiselect(groupBy, builder.count(root))
                .where(builder.and(predicates.toArray(new Predicate[0])));
        return criteria;
    }
}
