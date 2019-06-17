package com.ecaservice.filter;

import com.ecaservice.web.dto.MatchModeVisitor;
import com.ecaservice.web.dto.model.FilterRequestDto;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.beans.PropertyDescriptor;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.ecaservice.util.Utils.splitByPointSeparator;

/**
 * Implements filter based on JPA specification.
 *
 * @author Roman Batygin
 */
public abstract class AbstractFilter<T> implements Specification<T> {

    private static final String LIKE_FORMAT = "%{0}%";

    /**
     * Entity class
     */
    private Class<T> clazz;

    /**
     * Search query string for global filter
     */
    @Getter
    private String searchQuery;

    /**
     * Global filter fields list
     */
    @Getter
    private List<String> globalFilterFields;

    /**
     * Filters requests
     */
    @Getter
    private List<FilterRequestDto> filters;

    protected AbstractFilter(Class<T> clazz, String searchQuery, List<String> globalFilterFields,
                             List<FilterRequestDto> filters) {
        this.clazz = clazz;
        this.searchQuery = searchQuery;
        this.globalFilterFields = globalFilterFields;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = buildPredicates(root, criteriaBuilder);
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private List<Predicate> buildPredicates(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotBlank(searchQuery) && !CollectionUtils.isEmpty(globalFilterFields)) {
            predicates.add(buildPredicateForGlobalFilter(root, criteriaBuilder));
        }
        if (!CollectionUtils.isEmpty(filters)) {
            predicates.addAll(buildPredicatesForFilters(root, criteriaBuilder));
        }
        return predicates;
    }

    private Predicate buildPredicateForGlobalFilter(Root<T> root, CriteriaBuilder criteriaBuilder) {
        Predicate[] predicates = globalFilterFields.stream().map(field -> {
            Expression<String> expression = buildExpression(root, field);
            return criteriaBuilder.like(criteriaBuilder.lower(expression),
                    MessageFormat.format(LIKE_FORMAT, searchQuery.trim().toLowerCase()));
        }).toArray(Predicate[]::new);
        return criteriaBuilder.or(predicates);
    }

    private List<Predicate> buildPredicatesForFilters(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        filters.forEach(filterRequestDto -> {
            if (StringUtils.isNotBlank(filterRequestDto.getValue())) {
                predicates.add(buildPredicate(filterRequestDto, root, criteriaBuilder));
            }
        });
        return predicates;
    }

    private Predicate buildPredicate(FilterRequestDto filterRequestDto, Root<T> root, CriteriaBuilder criteriaBuilder) {
        return filterRequestDto.getMatchMode().handle(new MatchModeVisitor<Predicate>() {
            @Override
            public Predicate caseEquals() {
                return buildEqualPredicate(filterRequestDto, root, criteriaBuilder);
            }

            @Override
            public Predicate caseGte() {
                return buildGreaterThanOrEqualPredicate(filterRequestDto, root, criteriaBuilder);
            }

            @Override
            public Predicate caseLte() {
                return buildLessThanOrEqualPredicate(filterRequestDto, root, criteriaBuilder);
            }

            @Override
            public Predicate caseLike() {
                return buildLikePredicate(filterRequestDto, root, criteriaBuilder);
            }
        });
    }

    private Predicate buildGreaterThanOrEqualPredicate(FilterRequestDto filterRequestDto, Root<T> root,
                                                       CriteriaBuilder criteriaBuilder) {
        String value = filterRequestDto.getValue().trim();
        switch (filterRequestDto.getFilterType()) {
            case DATE:
                Expression<LocalDateTime> expression = buildExpression(root, filterRequestDto.getName());
                LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                return criteriaBuilder.greaterThanOrEqualTo(expression, localDate.atStartOfDay());
            default:
                return criteriaBuilder.greaterThanOrEqualTo(buildExpression(root, filterRequestDto.getName()), value);
        }
    }

    private Predicate buildLessThanOrEqualPredicate(FilterRequestDto filterRequestDto, Root<T> root,
                                                    CriteriaBuilder criteriaBuilder) {
        String value = filterRequestDto.getValue().trim();
        switch (filterRequestDto.getFilterType()) {
            case DATE:
                Expression<LocalDateTime> expression = buildExpression(root, filterRequestDto.getName());
                LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                return criteriaBuilder.lessThanOrEqualTo(expression, localDate.atTime(LocalTime.MAX));
            default:
                return criteriaBuilder.lessThanOrEqualTo(buildExpression(root, filterRequestDto.getName()), value);
        }
    }

    private Predicate buildEqualPredicate(FilterRequestDto filterRequestDto, Root<T> root,
                                          CriteriaBuilder criteriaBuilder) {
        String value = filterRequestDto.getValue().trim();
        Expression<?> expression = buildExpression(root, filterRequestDto.getName());
        switch (filterRequestDto.getFilterType()) {
            case REFERENCE:
                try {
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(filterRequestDto.getName(), clazz);
                    String getter = propertyDescriptor.getReadMethod().getName();
                    Class enumClazz = clazz.getMethod(getter).getReturnType();
                    return criteriaBuilder.equal(expression, Enum.valueOf(enumClazz, value));
                } catch (Exception ex) {
                    throw new IllegalArgumentException(ex.getMessage());
                }
            default:
                return criteriaBuilder.equal(expression, value);
        }
    }

    private Predicate buildLikePredicate(FilterRequestDto filterRequestDto, Root<T> root,
                                         CriteriaBuilder criteriaBuilder) {
        Expression<String> expression = buildExpression(root, filterRequestDto.getName());
        return criteriaBuilder.like(criteriaBuilder.lower(expression),
                MessageFormat.format(LIKE_FORMAT, filterRequestDto.getValue().trim().toLowerCase()));
    }

    private <E> Expression<E> buildExpression(Root<T> root, String fieldName) {
        String[] fieldLevels = splitByPointSeparator(fieldName);
        if (fieldLevels.length > 1) {
            Join<T, ?> join = root.join(fieldLevels[0]);
            return join.get(fieldLevels[1]);
        } else {
            return root.get(fieldName);
        }
    }
}
