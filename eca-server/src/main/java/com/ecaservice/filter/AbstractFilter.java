package com.ecaservice.filter;

import com.ecaservice.web.dto.MatchModeVisitor;
import com.ecaservice.web.dto.model.FilterRequestDto;
import eca.core.DescriptiveEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ecaservice.util.ReflectionUtils.getFieldType;
import static com.ecaservice.util.Utils.splitByPointSeparator;
import static com.google.common.collect.Lists.newArrayList;

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
        Predicate[] predicates = globalFilterFields.stream().map(
                field -> buildSinglePredicateForGlobalFilter(root, criteriaBuilder, field, trimQuery)).filter(
                Objects::nonNull).toArray(Predicate[]::new);
        return criteriaBuilder.or(predicates);
    }

    private List<Predicate> buildPredicatesForFilters(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = newArrayList();
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
        Class<?> fieldClazz = getFieldType(filterRequestDto.getName(), clazz);
        if (fieldClazz.isEnum()) {
            throw new IllegalStateException(String.format("Can't build GTE predicate for filter field [%s] of class %s",
                    filterRequestDto.getName(), clazz.getName()));
        } else if (LocalDateTime.class.isAssignableFrom(fieldClazz)) {
            Expression<LocalDateTime> expression = buildExpression(root, filterRequestDto.getName());
            LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
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
            LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
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
                LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                return criteriaBuilder.between(expression, localDate.atStartOfDay(),
                        localDate.atTime(LocalTime.MAX));
            }).toArray(Predicate[]::new);
            return criteriaBuilder.or(predicates);
        } else if (fieldClazz.isEnum()) {
            Expression<?> expression = buildExpression(root, filterRequestDto.getName());
            return expression.in(
                    values.stream().map(value -> Enum.valueOf(fieldClazz, value)).collect(Collectors.toList()));
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
            Predicate[] predicates =
                    values.stream().map(value -> criteriaBuilder.like(criteriaBuilder.lower(expression),
                            MessageFormat.format(LIKE_FORMAT, value.toLowerCase()))).toArray(Predicate[]::new);
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

    private <E> Expression<E> buildExpression(Root<T> root, String fieldName) {
        String[] fieldLevels = splitByPointSeparator(fieldName);
        if (fieldLevels != null && fieldLevels.length > 1) {
            Join<T, ?> join = root.join(fieldLevels[0]);
            return join.get(fieldLevels[1]);
        } else {
            return root.get(fieldName);
        }
    }

    private Predicate buildSinglePredicateForGlobalFilter(Root<T> root, CriteriaBuilder criteriaBuilder, String field,
                                                          String value) {
        Class<?> fieldClazz = getFieldType(field, clazz);
        if (fieldClazz.isEnum()) {
            if (!DescriptiveEnum.class.isAssignableFrom(fieldClazz)) {
                throw new IllegalStateException(
                        String.format("Enum class [%s] must implements [%s] interface!", fieldClazz.getSimpleName(),
                                DescriptiveEnum.class.getSimpleName()));
            }
            return buildGlobalFilterPredicateForEnumField(fieldClazz, root, field, value);
        } else if (String.class.isAssignableFrom(fieldClazz)) {
            Expression<String> expression = buildExpression(root, field);
            return criteriaBuilder.like(criteriaBuilder.lower(expression),
                    MessageFormat.format(LIKE_FORMAT, value));
        } else {
            throw new IllegalStateException(
                    String.format("Can't build LIKE predicate for filter field [%s] of class %s", field,
                            clazz.getName()));
        }
    }

    private Predicate buildGlobalFilterPredicateForEnumField(Class<?> fieldClazz, Root<T> root, String field,
                                                             String value) {
        List<DescriptiveEnum> descriptiveEnums =
                Stream.of(fieldClazz.getEnumConstants()).map(DescriptiveEnum.class::cast).filter(
                        val -> val.getDescription().toLowerCase().contains(value)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(descriptiveEnums)) {
            Expression<?> expression = buildExpression(root, field);
            return expression.in(descriptiveEnums);
        }
        return null;
    }
}
