package com.ecaservice.filter;

import com.ecaservice.web.dto.FilterFieldTypeVisitor;
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
import java.util.stream.Collectors;

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
        String trimQuery = searchQuery.trim().toLowerCase();
        Predicate[] predicates = globalFilterFields.stream().map(field -> {
            Expression<String> expression = buildExpression(root, field);
            return criteriaBuilder.like(criteriaBuilder.lower(expression),
                    MessageFormat.format(LIKE_FORMAT, trimQuery));
        }).toArray(Predicate[]::new);
        return criteriaBuilder.or(predicates);
    }

    private List<Predicate> buildPredicatesForFilters(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        filters.forEach(filterRequestDto -> {
            if (!CollectionUtils.isEmpty(filterRequestDto.getValues())) {
                List<String> values = filterRequestDto.getValues().stream().filter(StringUtils::isNotBlank).map(
                        String::trim).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(values)) {
                    predicates.add(buildPredicate(filterRequestDto, values, root, criteriaBuilder));
                }
            }
        });
        return predicates;
    }

    private Predicate buildPredicate(FilterRequestDto filterRequestDto, List<String> values, Root<T> root,
                                     CriteriaBuilder criteriaBuilder) {
        return filterRequestDto.getMatchMode().handle(new MatchModeVisitor<Predicate>() {
            @Override
            public Predicate caseEquals() {
                return buildEqualPredicate(filterRequestDto, values, root, criteriaBuilder);
            }

            @Override
            public Predicate caseLike() {
                return buildLikePredicate(filterRequestDto, values, root, criteriaBuilder);
            }

            @Override
            public Predicate caseRange() {
                return buildRangePredicate(filterRequestDto, values, root, criteriaBuilder);
            }
        });
    }

    private Predicate buildGreaterThanOrEqualPredicate(FilterRequestDto filterRequestDto, String value, Root<T> root,
                                                       CriteriaBuilder criteriaBuilder) {
        return filterRequestDto.getFilterFieldType().handle(new FilterFieldTypeVisitor<Predicate>() {
            @Override
            public Predicate caseText() {
                return criteriaBuilder.greaterThanOrEqualTo(buildExpression(root, filterRequestDto.getName()), value);
            }

            @Override
            public Predicate caseDate() {
                Expression<LocalDateTime> expression = buildExpression(root, filterRequestDto.getName());
                LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                return criteriaBuilder.greaterThanOrEqualTo(expression, localDate.atStartOfDay());
            }

            @Override
            public Predicate caseReference() {
                throw new UnsupportedOperationException(
                        String.format("Can't build GTE predicate for filter field with type: %s",
                                filterRequestDto.getFilterFieldType()));
            }
        });
    }

    private Predicate buildLessThanOrEqualPredicate(FilterRequestDto filterRequestDto, String value, Root<T> root,
                                                    CriteriaBuilder criteriaBuilder) {
        return filterRequestDto.getFilterFieldType().handle(new FilterFieldTypeVisitor<Predicate>() {
            @Override
            public Predicate caseText() {
                return criteriaBuilder.lessThanOrEqualTo(buildExpression(root, filterRequestDto.getName()), value);
            }

            @Override
            public Predicate caseDate() {
                Expression<LocalDateTime> expression = buildExpression(root, filterRequestDto.getName());
                LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                return criteriaBuilder.lessThanOrEqualTo(expression, localDate.atTime(LocalTime.MAX));
            }

            @Override
            public Predicate caseReference() {
                throw new UnsupportedOperationException(
                        String.format("Can't build LTE predicate for filter field with type: %s",
                                filterRequestDto.getFilterFieldType()));
            }
        });
    }

    private Predicate buildEqualPredicate(FilterRequestDto filterRequestDto, List<String> values, Root<T> root,
                                          CriteriaBuilder criteriaBuilder) {
        Expression<?> expression = buildExpression(root, filterRequestDto.getName());
        return filterRequestDto.getFilterFieldType().handle(new FilterFieldTypeVisitor<Predicate>() {
            @Override
            public Predicate caseText() {
                return expression.in(values);
            }

            @Override
            public Predicate caseDate() {
                Predicate[] predicates = values.stream().map(value -> {
                    Predicate lowerBoundPredicate =
                            buildGreaterThanOrEqualPredicate(filterRequestDto, value, root, criteriaBuilder);
                    Predicate upperBoundPredicate =
                            buildLessThanOrEqualPredicate(filterRequestDto, value, root, criteriaBuilder);
                    return criteriaBuilder.and(lowerBoundPredicate, upperBoundPredicate);
                }).toArray(Predicate[]::new);
                return criteriaBuilder.or(predicates);
            }

            @Override
            public Predicate caseReference() {
                try {
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(filterRequestDto.getName(), clazz);
                    String getter = propertyDescriptor.getReadMethod().getName();
                    Class enumClazz = clazz.getMethod(getter).getReturnType();
                    return expression.in(
                            values.stream().map(value -> Enum.valueOf(enumClazz, value)).collect(Collectors.toList()));
                } catch (Exception ex) {
                    throw new IllegalStateException(ex.getMessage());
                }
            }
        });
    }

    private Predicate buildLikePredicate(FilterRequestDto filterRequestDto, List<String> values, Root<T> root,
                                         CriteriaBuilder criteriaBuilder) {
        Expression<String> expression = buildExpression(root, filterRequestDto.getName());
        Predicate[] predicates = values.stream().map(value -> criteriaBuilder.like(criteriaBuilder.lower(expression),
                MessageFormat.format(LIKE_FORMAT, value.toLowerCase()))).toArray(Predicate[]::new);
        return criteriaBuilder.or(predicates);
    }

    private Predicate buildRangePredicate(FilterRequestDto filterRequestDto, List<String> values, Root<T> root,
                                          CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(buildGreaterThanOrEqualPredicate(filterRequestDto, values.get(0), root, criteriaBuilder));
        if (values.size() >= 1) {
            predicates.add(buildLessThanOrEqualPredicate(filterRequestDto, values.get(1), root, criteriaBuilder));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
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