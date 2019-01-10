package com.ecaservice.filter;

import com.ecaservice.web.dto.MatchModeVisitor;
import com.ecaservice.web.dto.model.FilterRequestDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.beans.PropertyDescriptor;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements filter based on JPA specification.
 *
 * @author Roman Batygin
 */
public abstract class AbstractFilter<T> implements Specification<T> {

    /**
     * Entity class
     */
    private Class<T> clazz;

    /**
     * Filters requests
     */
    @Setter
    @Getter
    private List<FilterRequestDto> filters;

    protected AbstractFilter(Class<T> clazz, List<FilterRequestDto> filters) {
        this.clazz = clazz;
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = buildPredicates(root, criteriaBuilder);
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private List<Predicate> buildPredicates(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (!CollectionUtils.isEmpty(filters)) {
            filters.forEach(filterRequestDto -> {
                if (StringUtils.isNotBlank(filterRequestDto.getValue())) {
                    predicates.add(buildPredicate(filterRequestDto, root, criteriaBuilder));
                }
            });
        }
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
                return criteriaBuilder.like(criteriaBuilder.lower(root.get(filterRequestDto.getName())),
                        MessageFormat.format("%{0}%", filterRequestDto.getValue().trim().toLowerCase()));
            }
        });
    }

    private Predicate buildGreaterThanOrEqualPredicate(FilterRequestDto filterRequestDto, Root<T> root,
                                                       CriteriaBuilder criteriaBuilder) {
        switch (filterRequestDto.getFilterType()) {
            case DATE:
                LocalDate localDate = LocalDate.parse(filterRequestDto.getValue(), DateTimeFormatter.ISO_LOCAL_DATE);
                return criteriaBuilder.greaterThanOrEqualTo(root.get(filterRequestDto.getName()),
                        localDate.atStartOfDay());
            default:
                return criteriaBuilder.greaterThanOrEqualTo(root.get(filterRequestDto.getName()),
                        filterRequestDto.getValue());
        }
    }

    private Predicate buildLessThanOrEqualPredicate(FilterRequestDto filterRequestDto, Root<T> root,
                                                    CriteriaBuilder criteriaBuilder) {
        switch (filterRequestDto.getFilterType()) {
            case DATE:
                LocalDate localDate = LocalDate.parse(filterRequestDto.getValue(), DateTimeFormatter.ISO_LOCAL_DATE);
                return criteriaBuilder.lessThanOrEqualTo(root.get(filterRequestDto.getName()),
                        localDate.atTime(LocalTime.MAX));
            default:
                return criteriaBuilder.lessThanOrEqualTo(root.get(filterRequestDto.getName()),
                        filterRequestDto.getValue());
        }
    }

    private Predicate buildEqualPredicate(FilterRequestDto filterRequestDto, Root<T> root,
                                          CriteriaBuilder criteriaBuilder) {
        switch (filterRequestDto.getFilterType()) {
            case REFERENCE:
                try {
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(filterRequestDto.getName(), clazz);
                    String getter = propertyDescriptor.getReadMethod().getName();
                    Class enumClazz = clazz.getMethod(getter).getReturnType();
                    return criteriaBuilder.equal(root.get(filterRequestDto.getName()),
                            Enum.valueOf(enumClazz, filterRequestDto.getValue()));
                } catch (Exception ex) {
                    throw new IllegalArgumentException(ex.getMessage());
                }
            default:
                return criteriaBuilder.equal(root.get(filterRequestDto.getName()), filterRequestDto.getValue().trim());
        }
    }
}
