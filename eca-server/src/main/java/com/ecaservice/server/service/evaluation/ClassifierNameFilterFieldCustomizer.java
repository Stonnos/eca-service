package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.core.filter.specification.FilterFieldCustomizer;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildExpression;
import static com.ecaservice.server.service.filter.dictionary.FilterDictionaries.CLASSIFIER_NAME;

/**
 * Classifier name filter field customizer.
 *
 * @author Roman Batygin
 */
public class ClassifierNameFilterFieldCustomizer extends FilterFieldCustomizer {

    private static final String CLASSIFIER_NAME_FIELD = "classifierInfo.classifierName";

    private final FilterService filterService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterService - filter service
     */
    public ClassifierNameFilterFieldCustomizer(FilterService filterService) {
        super(CLASSIFIER_NAME_FIELD);
        this.filterService = filterService;
    }

    @Override
    public Predicate toPredicate(Root<?> root, CriteriaBuilder criteriaBuilder, String value) {
        var classifiersDictionary = filterService.getFilterDictionary(CLASSIFIER_NAME);
        var classifierNames = classifiersDictionary.getValues()
                .stream()
                .filter(filterDictionaryValueDto -> filterDictionaryValueDto.getLabel().toLowerCase().contains(
                        value.toLowerCase()))
                .map(FilterDictionaryValueDto::getValue)
                .collect(Collectors.toList());
        Expression<?> expression = buildExpression(root, getFieldName());
        return expression.in(classifierNames);
    }
}
