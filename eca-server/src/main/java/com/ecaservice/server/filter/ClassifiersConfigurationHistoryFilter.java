package com.ecaservice.server.filter;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity;
import com.ecaservice.web.dto.model.FilterRequestDto;
import lombok.Getter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

import static com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity_.CONFIGURATION;

/**
 * Implements classifiers configuration history filter.
 *
 * @author Roman Batygin
 */
public class ClassifiersConfigurationHistoryFilter extends AbstractFilter<ClassifiersConfigurationHistoryEntity> {

    @Getter
    private final ClassifiersConfiguration classifiersConfiguration;

    /**
     * Constructor with filters requests.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     * @param globalFilterFields       - global filter fields list
     * @param searchQuery              - search query string
     * @param filters                  - filters requests list
     */
    public ClassifiersConfigurationHistoryFilter(ClassifiersConfiguration classifiersConfiguration,
                                                 String searchQuery,
                                                 List<String> globalFilterFields,
                                                 List<FilterRequestDto> filters) {
        super(ClassifiersConfigurationHistoryEntity.class, searchQuery, globalFilterFields, filters);
        this.classifiersConfiguration = classifiersConfiguration;
    }

    @Override
    public Predicate toPredicate(Root<ClassifiersConfigurationHistoryEntity> root,
                                 CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder criteriaBuilder) {
        var predicate = super.toPredicate(root, criteriaQuery, criteriaBuilder);
        var configurationExpression = root.get(CONFIGURATION);
        var configurationEqPredicate = criteriaBuilder.equal(configurationExpression, classifiersConfiguration);
        return criteriaBuilder.and(configurationEqPredicate, predicate);
    }
}
