package com.ecaservice.core.filter.specification;

import com.ecaservice.web.dto.MatchModeVisitor;
import com.ecaservice.web.dto.model.FilterRequestDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.ReflectionUtils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ecaservice.core.filter.util.FilterUtils.buildExpression;
import static com.ecaservice.core.filter.util.ReflectionUtils.getFieldType;
import static com.ecaservice.core.filter.util.Utils.parseDate;
import static com.ecaservice.core.filter.util.Utils.valueOf;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Implements filter based on JPA specification.
 *
 * @author Roman Batygin
 */
public abstract class AbstractFilter<T> implements Specification<T> {

    private static final String LIKE_FORMAT = "%{0}%";
    private static final String GET_ENUM_DESCRIPTION_METHOD_NAME = "getDescription";

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

    /**
     * Global filter fields customizers
     */
    @Getter
    @Setter
    private List<FilterFieldCustomizer> globalFilterFieldsCustomizers;

    protected AbstractFilter(Class<T> clazz, String searchQuery, List<String> globalFilterFields,
                             List<FilterRequestDto> filters) {
        Assert.notNull(clazz, "Class isn't specified!");
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
        List<Predicate> predicates = newArrayList();
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
        Predicate[] predicates = globalFilterFields.stream()
                .map(field -> buildSinglePredicateForGlobalFilter(root, criteriaBuilder, field, trimQuery))
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new);
        return criteriaBuilder.or(predicates);
    }

    private List<Predicate> buildPredicatesForFilters(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = newArrayList();
        filters.forEach(filterRequestDto -> {
            if (!CollectionUtils.isEmpty(filterRequestDto.getValues())) {
                List<String> values = filterRequestDto.getValues()
                        .stream()
                        .filter(StringUtils::isNotBlank)
                        .map(String::trim)
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(values)) {
                    predicates.add(buildPredicate(filterRequestDto, values, root, criteriaBuilder));
                }
            }
        });
        return predicates;
    }

    private Predicate buildPredicate(FilterRequestDto filterRequestDto, List<String> values, Root<T> root,
                                     CriteriaBuilder criteriaBuilder) {
        return filterRequestDto.getMatchMode().handle(new MatchModeVisitor<>() {
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
        Class<?> fieldClazz = getFieldType(filterRequestDto.getName(), clazz);
        if (fieldClazz.isEnum()) {
            throw new IllegalStateException(String.format("Can't build GTE predicate for filter field [%s] of class %s",
                    filterRequestDto.getName(), clazz.getName()));
        } else if (LocalDateTime.class.isAssignableFrom(fieldClazz)) {
            Expression<LocalDateTime> expression = buildExpression(root, filterRequestDto.getName());
            LocalDate localDate = parseDate(filterRequestDto.getName(), value);
            return criteriaBuilder.greaterThanOrEqualTo(expression, localDate.atStartOfDay());
        } else {
            return criteriaBuilder.greaterThanOrEqualTo(buildExpression(root, filterRequestDto.getName()), value);
        }
    }

    private Predicate buildLessThanOrEqualPredicate(FilterRequestDto filterRequestDto, String value, Root<T> root,
                                                    CriteriaBuilder criteriaBuilder) {
        Class<?> fieldClazz = getFieldType(filterRequestDto.getName(), clazz);
        if (fieldClazz.isEnum()) {
            throw new IllegalStateException(String.format("Can't build LTE predicate for filter field [%s] of class %s",
                    filterRequestDto.getName(), clazz.getName()));
        } else if (LocalDateTime.class.isAssignableFrom(fieldClazz)) {
            Expression<LocalDateTime> expression = buildExpression(root, filterRequestDto.getName());
            LocalDate localDate = parseDate(filterRequestDto.getName(), value);
            return criteriaBuilder.lessThanOrEqualTo(expression, localDate.atTime(LocalTime.MAX));
        } else {
            return criteriaBuilder.lessThanOrEqualTo(buildExpression(root, filterRequestDto.getName()), value);
        }
    }

    private Predicate buildEqualPredicate(FilterRequestDto filterRequestDto, List<String> values, Root<T> root,
                                          CriteriaBuilder criteriaBuilder) {
        Class fieldClazz = getFieldType(filterRequestDto.getName(), clazz);
        if (LocalDateTime.class.isAssignableFrom(fieldClazz)) {
            Predicate[] predicates = values.stream().map(value -> {
                Expression<LocalDateTime> expression = buildExpression(root, filterRequestDto.getName());
                LocalDate localDate = parseDate(filterRequestDto.getName(), value);
                return criteriaBuilder.between(expression, localDate.atStartOfDay(),
                        localDate.atTime(LocalTime.MAX));
            }).toArray(Predicate[]::new);
            return criteriaBuilder.or(predicates);
        } else if (fieldClazz.isEnum()) {
            Expression<?> expression = buildExpression(root, filterRequestDto.getName());
            return expression.in(
                    values.stream()
                            .map(value -> valueOf(filterRequestDto.getName(), fieldClazz, value))
                            .collect(Collectors.toList())
            );
        } else {
            Expression<String> expression = buildExpression(root, filterRequestDto.getName());
            return expression.in(values);
        }
    }

    private Predicate buildLikePredicate(FilterRequestDto filterRequestDto, List<String> values, Root<T> root,
                                         CriteriaBuilder criteriaBuilder) {
        Class<?> fieldClazz = getFieldType(filterRequestDto.getName(), clazz);
        if (!String.class.isAssignableFrom(fieldClazz)) {
            throw new IllegalStateException(
                    String.format("Can't build LIKE predicate for filter field [%s] of class %s",
                            filterRequestDto.getName(), clazz.getName()));
        } else {
            Expression<String> expression = buildExpression(root, filterRequestDto.getName());
            Predicate[] predicates = values.stream()
                    .map(value -> criteriaBuilder.like(criteriaBuilder.lower(expression),
                            MessageFormat.format(LIKE_FORMAT, value.toLowerCase())))
                    .toArray(Predicate[]::new);
            return criteriaBuilder.or(predicates);
        }
    }

    private Predicate buildRangePredicate(FilterRequestDto filterRequestDto, List<String> values, Root<T> root,
                                          CriteriaBuilder criteriaBuilder) {
        Predicate predicate = buildGreaterThanOrEqualPredicate(filterRequestDto, values.get(0), root, criteriaBuilder);
        if (values.size() > 1) {
            Predicate upperBoundPredicate =
                    buildLessThanOrEqualPredicate(filterRequestDto, values.get(1), root, criteriaBuilder);
            predicate = criteriaBuilder.and(predicate, upperBoundPredicate);
        }
        return predicate;
    }

    private Predicate buildSinglePredicateForGlobalFilter(Root<T> root, CriteriaBuilder criteriaBuilder, String field,
                                                          String value) {
        var filterFieldCustomizer = getGlobalFilterFieldCustomizer(field);
        if (filterFieldCustomizer != null) {
            return filterFieldCustomizer.toPredicate(root, criteriaBuilder, value);
        } else {
            Class<?> fieldClazz = getFieldType(field, clazz);
            if (fieldClazz.isEnum()) {
                return buildGlobalFilterPredicateForEnumField(fieldClazz, root, field, value);
            } else if (String.class.isAssignableFrom(fieldClazz)) {
                Expression<String> expression = buildExpression(root, field);
                return criteriaBuilder.like(criteriaBuilder.lower(expression),
                        MessageFormat.format(LIKE_FORMAT, value));
            } else if (Long.class.isAssignableFrom(fieldClazz)) {
                return buildGlobalFilterPredicateForNumericField(root, criteriaBuilder, field, value);
            } else {
                throw new IllegalStateException(
                        String.format("Can't build LIKE predicate for filter field [%s] of class %s", field,
                                clazz.getName()));
            }
        }
    }

    private FilterFieldCustomizer getGlobalFilterFieldCustomizer(String field) {
        if (CollectionUtils.isEmpty(globalFilterFieldsCustomizers)) {
            return null;
        }
        return globalFilterFieldsCustomizers.stream()
                .filter(filterFieldCustomizer -> filterFieldCustomizer.canHandle(field))
                .findFirst()
                .orElse(null);
    }

    private boolean enumDescriptionContainsSearchTerm(Method method, Object enumValue, String value) {
        Object retVal = ReflectionUtils.invokeMethod(method, enumValue);
        return retVal != null && String.valueOf(retVal).toLowerCase().contains(value);
    }

    private Predicate buildGlobalFilterPredicateForNumericField(Root<T> root, CriteriaBuilder criteriaBuilder,
                                                                String field, String value) {
        if (!StringUtils.isNumeric(value)) {
            return null;
        } else {
            Long longValue = NumberUtils.parseNumber(value, Long.class);
            Expression<String> expression = buildExpression(root, field);
            return criteriaBuilder.equal(expression, longValue);
        }
    }

    private Predicate buildGlobalFilterPredicateForEnumField(Class<?> fieldClazz, Root<T> root, String field,
                                                             String value) {
        Method method = ReflectionUtils.findMethod(fieldClazz, GET_ENUM_DESCRIPTION_METHOD_NAME);
        if (method != null) {
            List<Object> descriptiveEnums = Stream.of(fieldClazz.getEnumConstants())
                    .filter(c -> enumDescriptionContainsSearchTerm(method, c, value))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(descriptiveEnums)) {
                Expression<?> expression = buildExpression(root, field);
                return expression.in(descriptiveEnums);
            }
        }
        return null;
    }
}
